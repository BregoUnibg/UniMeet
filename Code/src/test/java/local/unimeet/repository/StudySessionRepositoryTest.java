package local.unimeet.repository;

import local.unimeet.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class StudySessionRepositoryTest {

    @Autowired
    private StudySessionRepository sessionRepo;

    @Autowired
    private TestEntityManager entityManager;

    // Shared infrastructure for all tests
    private StudyTable table1;
    private StudyTable table2;

    @BeforeEach
    void setupInfrastructure() {
        University uni = new University("Unibg");
        entityManager.persist(uni);

        Building building = new Building("Building A", "Main St", uni);
        entityManager.persist(building);

        Room room = new Room(101, building);
        entityManager.persist(room);

        table1 = new StudyTable(1, 4, room);
        entityManager.persist(table1);

        table2 = new StudyTable(2, 6, room);
        entityManager.persist(table2);
        
        //Every test starts with this existing enviroment
    }

    @Test
    @DisplayName("Find upcoming sessions owned by user")
    void testFindUpcomingOwnedSessions() {
        User user = createUser("owner1");
        
        // Linking to the shared table1
        createSession(user, table1, LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0)); 
        createSession(user, table1, LocalDate.now().minusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0));

        List<StudySession> result = sessionRepo.findUpcomingOwnedSessions("owner1");

        assertEquals(1, result.size());
        assertTrue(result.get(0).getDate().isAfter(LocalDate.now()));
    }

    @Test
    @DisplayName("Find all ended sessions in which user took part")
    void testFindAllMyEndedSessions() {
        User user = createUser("historyUser");

        createSession(user, table2, LocalDate.now().minusDays(1), LocalTime.of(9, 0), LocalTime.of(11, 0)); 
        createSession(user, table2, LocalDate.now().plusDays(1), LocalTime.of(9, 0), LocalTime.of(11, 0));

        List<StudySession> result = sessionRepo.findAllMyEndedHistory("historyUser");

        assertEquals(1, result.size());
        assertTrue(result.get(0).getDate().isBefore(LocalDate.now()));
    }

    @Test
    @DisplayName("Load session with full details (JOIN fething everything)")
    void testFindSessionWithDetailsById() {
        User user = createUser("detailUser");
        StudySession session = createSession(user, table1, LocalDate.now(), LocalTime.of(14, 0), LocalTime.of(16, 0));
        
        entityManager.flush();
        entityManager.clear();

        Optional<StudySession> result = sessionRepo.findSessionWithDetailsById(session.getId());

        assertTrue(result.isPresent());
        assertEquals("Unibg", result.get().getStudyTable().getRoom().getBuilding().getUniversity().getName());
        assertEquals("Building A", result.get().getBuilding().getName());
    }

    @Test
    @DisplayName("Find active sessions right now")
    void testFindActiveSessionsForUser() {
        User user = createUser("activeUser");

        createSession(user, table1, LocalDate.now(), LocalTime.now().minusMinutes(30), LocalTime.now().plusMinutes(30));

        List<StudySession> result = sessionRepo.findActiveSessionsForUser("activeUser");

        assertEquals(1, result.size());
    }

    
    private User createUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("pass");
        user.setRole(Role.USER);
        entityManager.persist(user);
        return user;
    }

    private StudySession createSession(User owner, StudyTable table, LocalDate date, LocalTime start, LocalTime end) {
        StudySession session = new StudySession();
        session.setOwner(owner);
        session.setStudyTable(table); 
        session.setDate(date);
        session.setStartTime(start);
        session.setEndTime(end);
        
        entityManager.persist(session);
        return session;
    }
}