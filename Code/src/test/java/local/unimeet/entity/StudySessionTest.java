package local.unimeet.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StudySessionTest {

    @Test
    @DisplayName("Returnign upcoming if is starts in the future")
    void testGetStatus_Upcoming() {
        StudySession session = new StudySession();
        
        session.setDate(LocalDate.now().plusDays(1));
        session.setStartTime(LocalTime.of(10, 0));
        session.setEndTime(LocalTime.of(12, 0));

        assertEquals(StudySessionStatus.UPCOMING, session.getStatus());
        
        session.setDate(LocalDate.now());
        session.setStartTime(LocalTime.now().plusMinutes(2));
        session.setEndTime(LocalTime.now().plusMinutes(12));

        assertEquals(StudySessionStatus.UPCOMING, session.getStatus());
    }

    @Test
    @DisplayName("Returning ended if session happend in the past")
    void testGetStatus_Ended() {
        StudySession session = new StudySession();
        
        session.setDate(LocalDate.now().minusDays(1));
        session.setStartTime(LocalTime.of(10, 0));
        session.setEndTime(LocalTime.of(12, 0));

        assertEquals(StudySessionStatus.ENDED, session.getStatus());
        
        session.setDate(LocalDate.now());
        session.setStartTime(LocalTime.now().minusMinutes(12));
        session.setEndTime(LocalTime.now().minusMinutes(2));

        assertEquals(StudySessionStatus.ENDED, session.getStatus());
        
    }

    @Test
    @DisplayName("A session stores both participants and owner")
    void testGetParticipantsAndOwner() {
        
    	StudySession session = new StudySession();
        
        User owner = new User();
        owner.setUsername("ownerUser");
        session.setOwner(owner);

        User participant1 = new User();
        participant1.setUsername("p1");
        
        session.addPartecipant(participant1);

        List<User> result = session.getParticipantsAndOwner();

        assertEquals(2, result.size());
        assertTrue(result.contains(owner));
        assertTrue(result.contains(participant1));
    }
}