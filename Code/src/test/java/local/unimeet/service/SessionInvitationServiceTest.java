package local.unimeet.service;

import local.unimeet.entity.*;
import local.unimeet.exception.StudentBusyElsewhereException;
import local.unimeet.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SessionInvitationServiceTest {

    @Autowired private SessionInvitationService inviteService;
    @MockBean private UserService userService;

    @Autowired private UserRepository userRepo;
    @Autowired private UniversityRepository uniRepo;
    @Autowired private BuildingRepository buildRepo;
    @Autowired private RoomRepository roomRepo;
    @Autowired private StudyTableRepository tableRepo;
    @Autowired private StudySessionRepository sessionRepo;
    @Autowired private SessionInvitationRepository inviteRepo;

    private User owner;
    private User invitee;
    private StudySession session;

    @BeforeEach
    void setup() {
        University uni = uniRepo.save(new University("UniTest"));
        Building b = buildRepo.save(new Building("B1", "Add", uni));
        Room r = roomRepo.save(new Room(101, b));
        StudyTable table = tableRepo.save(new StudyTable(1, 5, r));

        owner = userRepo.save(new User("owner", "pass", Role.USER));
        invitee = userRepo.save(new User("invitee", "pass", Role.USER));

        // Create Session
        session = new StudySession();
        session.setOwner(owner);
        session.setStudyTable(table);
        session.setDate(LocalDate.now().plusDays(1));
        session.setStartTime(LocalTime.of(10, 0));
        session.setEndTime(LocalTime.of(12, 0));
        
        // Manual list init for safety
        try {
            java.lang.reflect.Field f = StudySession.class.getDeclaredField("participants");
            f.setAccessible(true);
            f.set(session, new ArrayList<>());
        } catch (Exception e) {}

        session = sessionRepo.save(session);
    }

    @Test
    @DisplayName("Send invite successfully")
    void testSendInvite_Success() {
        inviteService.sendInvite(session, invitee);

        List<SessionInvitation> invites = inviteService.getPendingInvitationsByInvitee(invitee);
        assertEquals(1, invites.size());
        assertEquals(session.getId(), invites.get(0).getSession().getId());
    }

    @Test
    @DisplayName("Accept invite adds user to session")
    void testAcceptInvite() {
        
    	inviteService.sendInvite(session, invitee);
        Long inviteId = inviteService.getPendingInvitationsByInvitee(invitee).get(0).getId();

        inviteService.acceptInvite(inviteId);

        assertTrue(inviteRepo.findById(inviteId).isEmpty(), "Invite should be deleted");
        
        StudySession updatedSession = sessionRepo.findById(session.getId()).get();
        assertEquals(1, updatedSession.getParticipants().size());
        assertEquals("invitee", updatedSession.getParticipants().get(0).getUsername());
    }

    @Test
    @DisplayName("Fail to invite busy user")
    void testSendInvite_UserBusy() {
        
    	Room r2 = roomRepo.save(new Room(102, session.getStudyTable().getRoom().getBuilding()));
        StudyTable t2 = tableRepo.save(new StudyTable(2, 5, r2));

        StudySession otherSession = new StudySession();
        otherSession.setOwner(invitee); // Invitee is busy here
        otherSession.setStudyTable(t2);
        otherSession.setDate(session.getDate());
        otherSession.setStartTime(session.getStartTime());
        otherSession.setEndTime(session.getEndTime());
        sessionRepo.save(otherSession);

        // Try to invite to original session
        assertThrows(StudentBusyElsewhereException.class, () -> 
            inviteService.sendInvite(session, invitee)
        );
    }
}