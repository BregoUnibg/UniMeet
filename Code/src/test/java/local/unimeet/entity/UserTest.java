package local.unimeet.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("Asserting colleagues see eachothers")
    void testAddColleague() {
        
    	User user1 = new User();
        user1.setUsername("g.bregolin");
        
        User user2 = new User();
        user2.setUsername("s.vecchi");

        user1.addColleague(user2);

        assertTrue(user1.getColleagues().contains(user2));
        assertTrue(user2.getColleagues().contains(user1));
    }

    @Test
    @DisplayName("Aserting old colleagues no longer see eachother")
    void testRemoveColleague() {
        
    	User user1 = new User();
        user1.setUsername("g.bregolin");
        
        User user2 = new User();
        user2.setUsername("s.vecchi");
        
        user1.addColleague(user2);
        user1.removeColleague(user2);

        assertFalse(user1.getColleagues().contains(user2));
        assertFalse(user2.getColleagues().contains(user1));
    }

    @Test
    @DisplayName("Equals must cheack username")
    void testEqualsAndHashCode() {
        
        User u1 = new User();
        u1.setUsername("student1");
        
        User u2 = new User();
        u2.setUsername("student1"); 
        
        User u3 = new User();
        u3.setUsername("student2"); 

        assertEquals(u1, u2 );
        assertNotEquals(u1, u3);

    }
}