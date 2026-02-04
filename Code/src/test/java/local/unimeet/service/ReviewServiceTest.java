package local.unimeet.service;

import local.unimeet.entity.*;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ReviewServiceTest {

    @Autowired private ReviewService reviewService;
    @MockBean private UserService userService;

    @Autowired private UserRepository userRepo;
    @Autowired private UserProfileRepository profileRepo;
    @Autowired private UniversityRepository uniRepo;
    @Autowired private BuildingRepository buildRepo;
    @Autowired private RoomRepository roomRepo;
    @Autowired private StudyTableRepository tableRepo;
    @Autowired private StudySessionRepository sessionRepo;

    private User reviewer;
    private User target;
    private StudySession session;
    private StudyTable table;

    @BeforeEach
    void setup() {
        University uni = uniRepo.save(new University("UniTest"));
        Building b = buildRepo.save(new Building("B1", "Add", uni));
        Room r = roomRepo.save(new Room(101, b));
        table = tableRepo.save(new StudyTable(1, 10, r)); // Capacity 10

        reviewer = userRepo.save(new User("reviewer", "pass", Role.USER));
        target = userRepo.save(new User("target", "pass", Role.USER));
        
        // Initialize Target Profile
        UserProfile targetProfile = new UserProfile();
        targetProfile.setUser(target); 
        targetProfile.setReputation(0.0);
        targetProfile.setTotVoters(0);
        profileRepo.save(targetProfile);
        target.setProfile(targetProfile);
        userRepo.save(target);

        // Standard Session
        session = createSession(reviewer, table);
        addParticipantToSession(session, target);
    }

    @Test
    @DisplayName("Submit review successfully updates reputation")
    void testSubmitReview_Success() {
        reviewService.submitReview("reviewer", "target", session.getId(), 5);

        UserProfile updated = profileRepo.findByUser(target).get();
        assertEquals(5.0, updated.getReputation());
        assertEquals(1, updated.getTotVoters());
    }

    @Test
    @DisplayName("Deep Test: Verify weighted average and rounding with multiple reviews")
    void testReputationMath_MultipleReviews() {
        
    	User r1 = userRepo.save(new User("r1", "pass", Role.USER));
        User r2 = userRepo.save(new User("r2", "pass", Role.USER));
        User r3 = userRepo.save(new User("r3", "pass", Role.USER));

        addParticipantToSession(session, r1);
        addParticipantToSession(session, r2);
        addParticipantToSession(session, r3);

        reviewService.submitReview("r1", "target", session.getId(), 5);
        
        UserProfile p1 = profileRepo.findByUser(target).get();
        assertEquals(5.0, p1.getReputation());
        assertEquals(1, p1.getTotVoters());

        reviewService.submitReview("r2", "target", session.getId(), 3);
        
        UserProfile p2 = profileRepo.findByUser(target).get();
        assertEquals(4.0, p2.getReputation());
        assertEquals(2, p2.getTotVoters());

        reviewService.submitReview("r3", "target", session.getId(), 4);
        
        UserProfile p3 = profileRepo.findByUser(target).get();
        assertEquals(4.0, p3.getReputation());
        assertEquals(3, p3.getTotVoters());

        reviewService.submitReview("reviewer", "target", session.getId(), 5);

        UserProfile p4 = profileRepo.findByUser(target).get();
        assertEquals(4.25, p4.getReputation());
        assertEquals(4, p4.getTotVoters());
    }

    @Test
    @DisplayName("Fail if user is not participant")
    void testSubmitReview_NotParticipant() {
        User outsider = userRepo.save(new User("outsider", "pass", Role.USER));
        
        assertThrows(IllegalStateException.class, () -> 
            reviewService.submitReview("outsider", "target", session.getId(), 5)
        );
    }

    @Test
    @DisplayName("Fail if double voting")
    void testSubmitReview_DoubleVoting() {
        reviewService.submitReview("reviewer", "target", session.getId(), 5);

        assertThrows(IllegalStateException.class, () -> 
            reviewService.submitReview("reviewer", "target", session.getId(), 4)
        );
    }


    private StudySession createSession(User owner, StudyTable table) {
        StudySession s = new StudySession();
        s.setOwner(owner);
        s.setStudyTable(table);
        s.setDate(LocalDate.now().plusDays(1)); 
        s.setStartTime(LocalTime.of(10, 0));
        s.setEndTime(LocalTime.of(12, 0));
        
        // List init workaround
        try {
            java.lang.reflect.Field f = StudySession.class.getDeclaredField("participants");
            f.setAccessible(true);
            f.set(s, new ArrayList<>());
        } catch (Exception e) {}

        return sessionRepo.save(s);
    }

    private void addParticipantToSession(StudySession s, User u) {
        StudySession current = sessionRepo.findById(s.getId()).get();
        current.addPartecipant(u);
        sessionRepo.save(current);
    }
}