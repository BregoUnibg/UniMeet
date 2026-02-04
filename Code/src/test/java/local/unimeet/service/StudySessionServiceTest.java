package local.unimeet.service;

import local.unimeet.entity.*;
import local.unimeet.exception.StudentBusyElsewhereException;
import local.unimeet.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class StudySessionServiceTest {

    @Autowired private StudySessionService sessionService;
    @MockBean private UserService userService; // Mocking to avoid Security dependency

    @Autowired private UniversityRepository uniRepo;
    @Autowired private BuildingRepository buildRepo;
    @Autowired private RoomRepository roomRepo;
    @Autowired private StudyTableRepository tableRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private StudySessionRepository sessionRepo;

    // Test Data
    private User owner;
    private User student1;
    private User student2;
    private StudyTable table;
    private Room room;

    @BeforeEach
    void setupInfrastructure() {
        
    	University uni = uniRepo.save(new University("Test Poly"));
        Building b = buildRepo.save(new Building("Science Hall", "Main St", uni));
        room = roomRepo.save(new Room(101, b));
        
        table = tableRepo.save(new StudyTable(1, 3, room)); 

        owner = userRepo.save(new User("owner", "pass", Role.USER));
        student1 = userRepo.save(new User("student1", "pass", Role.USER));
        student2 = userRepo.save(new User("student2", "pass", Role.USER));

        when(userService.getUserByUsername("owner")).thenReturn(owner);
        when(userService.getUserByUsername("student1")).thenReturn(student1);
        when(userService.getUserByUsername("student2")).thenReturn(student2);
    }

    // ==========================================
    // VALIDATION & BASIC SAVING
    // ==========================================
    
    @Nested
    @DisplayName("Creation & Validation Logic")
    class CreationTests {

        @Test
        @DisplayName("Success: Save valid future session")
        void testSaveValidSession() {
            StudySession s = createSessionObject(LocalDate.now().plusDays(1), LocalTime.of(14, 0), LocalTime.of(16, 0));
            
            StudySession saved = sessionService.saveStudySession(s);
            assertNotNull(saved.getId());
            assertEquals(StudySessionStatus.UPCOMING, saved.getStatus());
        }

        @Test
        @DisplayName("Fail: End time before Start time")
        void testInvalidTimes() {
            StudySession s = createSessionObject(LocalDate.now().plusDays(1), LocalTime.of(16, 0), LocalTime.of(14, 0));
            
            assertThrows(IllegalArgumentException.class, () -> sessionService.saveStudySession(s));
        }

        @Test
        @DisplayName("Fail: Session in the past")
        void testPastDate() {
            StudySession s = createSessionObject(LocalDate.now().minusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0));
            
            assertThrows(IllegalArgumentException.class, () -> sessionService.saveStudySession(s));
        }
    }

    // ==========================================
    // SCHEDULING CONFLICTS
    // ==========================================
    
    @Nested
    @DisplayName("Scheduling & Overlap Logic")
    class SchedulingTests {

        @Test
        @DisplayName("Fail: Table occupied (Exact match)")
        void testTableOccupied_ExactMatch() {
            
        	sessionService.saveStudySession(createSessionObject(LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0)));

            StudySession s2 = createSessionObject(LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0));
            s2.setOwner(student1);

            assertThrows(IllegalStateException.class, () -> sessionService.saveStudySession(s2));
        }

        @Test
        @DisplayName("Fail: New session overlaps END of existing session")
        void testTableOccupied_OverlapEnd() {
            
        	sessionService.saveStudySession(createSessionObject(LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0)));

            StudySession s2 = createSessionObject(LocalDate.now().plusDays(1), LocalTime.of(11, 0), LocalTime.of(13, 0));
            s2.setOwner(student1);

            assertThrows(IllegalStateException.class, () -> sessionService.saveStudySession(s2));
        }

        @Test
        @DisplayName("Fail: New session overlaps START of existing session")
        void testTableOccupied_OverlapStart() {
            
        	sessionService.saveStudySession(createSessionObject(LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0)));

            StudySession s2 = createSessionObject(LocalDate.now().plusDays(1), LocalTime.of(9, 0), LocalTime.of(11, 0));
            s2.setOwner(student1);

            assertThrows(IllegalStateException.class, () -> sessionService.saveStudySession(s2));
        }

        @Test
        @DisplayName("Success: Session adjacent (Ends exactly when next starts)")
        void testAdjacentSessions() {

        	sessionService.saveStudySession(createSessionObject(LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0)));


        	StudySession s2 = createSessionObject(LocalDate.now().plusDays(1), LocalTime.of(12, 0), LocalTime.of(14, 0));
            s2.setOwner(student1);

            StudySession saved = sessionService.saveStudySession(s2);
            assertNotNull(saved.getId());
        }

        @Test
        @DisplayName("Fail: User (Owner) busy elsewhere at same time")
        void testOwnerBusyElsewhere() {

        	Room room2 = roomRepo.save(new Room(102, room.getBuilding()));
            StudyTable table2 = tableRepo.save(new StudyTable(2, 4, room2));

            sessionService.saveStudySession(createSessionObject(LocalDate.now().plusDays(2), LocalTime.of(10, 0), LocalTime.of(12, 0)));

            StudySession s2 = new StudySession();
            s2.setOwner(owner);
            s2.setStudyTable(table2);
            s2.setDate(LocalDate.now().plusDays(2));
            s2.setStartTime(LocalTime.of(10, 0));
            s2.setEndTime(LocalTime.of(12, 0));
            initParticipants(s2);

            assertThrows(StudentBusyElsewhereException.class, () -> sessionService.saveStudySession(s2));
        }
    }

    // ==========================================
    // PARTICIPANTS & CAPACITY
    // ==========================================
    @Nested
    @DisplayName("Participant Management")
    class ParticipantTests {

        @Test
        @DisplayName("Success: Add participants until capacity")
        void testAddParticipant_Success() {
            
        	StudySession session = sessionService.saveStudySession(createSessionObject(LocalDate.now().plusDays(1), LocalTime.of(14, 0), LocalTime.of(16, 0)));

            sessionService.addPartecipant(session, "student1");
            assertEquals(1, session.getParticipants().size());

            sessionService.addPartecipant(session, "student2");
            assertEquals(2, session.getParticipants().size());
        }

        @Test
        @DisplayName("Fail: Exceed table capacity")
        void testAddParticipant_CapacityExceeded() {

        	StudySession session = sessionService.saveStudySession(createSessionObject(LocalDate.now().plusDays(1), LocalTime.of(14, 0), LocalTime.of(16, 0)));
            sessionService.addPartecipant(session, "student1");
            sessionService.addPartecipant(session, "student2");

            User extraUser = userRepo.save(new User("extra", "pass", Role.USER));
            when(userService.getUserByUsername("extra")).thenReturn(extraUser);

            assertThrows(IllegalStateException.class, () -> sessionService.addPartecipant(session, "extra"));
            
        }

        @Test
        @DisplayName("Fail: User busy in another session")
        void testAddParticipant_UserBusy() {
            LocalDate date = LocalDate.now().plusDays(3);
            
            StudySession otherSession = new StudySession();
            otherSession.setOwner(student1);
            otherSession.setStudyTable(table); // Doesn't matter which table for user check
            otherSession.setDate(date);
            otherSession.setStartTime(LocalTime.of(10, 0));
            otherSession.setEndTime(LocalTime.of(12, 0));
            initParticipants(otherSession);
            sessionService.saveStudySession(otherSession);

            StudySession ownerSession = createSessionObject(date, LocalTime.of(10, 0), LocalTime.of(12, 0));
            
            StudyTable table2 = tableRepo.save(new StudyTable(99, 5, room));
            ownerSession.setStudyTable(table2);
            sessionService.saveStudySession(ownerSession);

            assertThrows(StudentBusyElsewhereException.class, () -> 
                sessionService.addPartecipant(ownerSession, "student1")
            );
            
        }

        @Test
        @DisplayName("Fail: Cannot add Owner as Participant")
        void testAddOwnerAsParticipant() {
            StudySession session = sessionService.saveStudySession(createSessionObject(LocalDate.now().plusDays(1), LocalTime.of(14, 0), LocalTime.of(16, 0)));

            assertThrows(IllegalArgumentException.class, () -> 
                sessionService.addPartecipant(session, "owner")
            );
        }
    }

    // ==========================================
    // DELETE & LIFECYCLE
    // ==========================================
    @Nested
    @DisplayName("Lifecycle Management")
    class LifecycleTests {

        @Test
        @DisplayName("Success: Delete UPCOMING session")
        void testDeleteUpcoming() {
            StudySession session = sessionService.saveStudySession(createSessionObject(LocalDate.now().plusDays(5), LocalTime.of(10, 0), LocalTime.of(12, 0)));
            
            sessionService.deleteSession(session);
            
            assertFalse(sessionRepo.existsById(session.getId()));
        }

        @Test
        @DisplayName("Fail: Cannot delete ENDED session")
        void testDeleteEnded() {

        	StudySession pastSession = createSessionObject(LocalDate.now().minusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0));
            pastSession = sessionRepo.save(pastSession); // Direct save
            
            StudySession fetched = sessionRepo.findById(pastSession.getId()).get();
            
            assertEquals(StudySessionStatus.ENDED, fetched.getStatus());

            assertThrows(IllegalStateException.class, () -> sessionService.deleteSession(fetched));
            
        }

        @Test
        @DisplayName("Fail: Delete non-existent session")
        void testDeleteNonExistent() {
            StudySession fakeSession = new StudySession();
            try {

            	java.lang.reflect.Field f = StudySession.class.getDeclaredField("id");
                f.setAccessible(true);
                f.set(fakeSession, 9999L);
                
            } catch (Exception e) {}

            assertThrows(NoSuchElementException.class, () -> sessionService.deleteSession(fakeSession));
        }
    }

    // ==========================================
    // HELPERS
    // ==========================================
    private StudySession createSessionObject(LocalDate date, LocalTime start, LocalTime end) {
        StudySession s = new StudySession();
        s.setOwner(owner);
        s.setStudyTable(table);
        s.setDate(date);
        s.setStartTime(start);
        s.setEndTime(end);
        initParticipants(s);
        return s;
    }

    // Helper to initialize list and avoid NPE in Service logic if entities aren't full loaded
    private void initParticipants(StudySession s) {
        try {
            java.lang.reflect.Field f = StudySession.class.getDeclaredField("participants");
            f.setAccessible(true);
            f.set(s, new ArrayList<>());
        } catch (Exception e) { e.printStackTrace(); }
    }
    
}