package local.unimeet.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserProfileTest {

    @Test
    @DisplayName("Correct rating with one vore")
    void testAddReviewRating_FirstVote() {
        
    	UserProfile profile = new UserProfile();
        assertEquals(0.0, profile.getReputation());

        profile.addReviewRating(5);

        //Verifing that one user made a 5 star review
        assertEquals(5.0, profile.getReputation());
        assertEquals(1, profile.getTotVoters());
    }

    @Test
    @DisplayName("Correct rating multiple votes")
    void testAddReviewRating_MultipleVotes() {
        UserProfile profile = new UserProfile();
        
        profile.addReviewRating(4); 
        profile.addReviewRating(5); 
        profile.addReviewRating(1);
        profile.addReviewRating(5);
        
        //15/4 = 3.75
        
        assertEquals(3.75, profile.getReputation());
        assertEquals(4, profile.getTotVoters());
    }

    @Test
    @DisplayName("Correct exception throwing if vote inserted is not valid")
    void testAddReviewRating_InvalidScore() {
        UserProfile profile = new UserProfile();

        //Score > 5 
        IllegalArgumentException exceptionHigh = assertThrows(IllegalArgumentException.class, () -> {
            profile.addReviewRating(6);
        });
        assertEquals("Score must be between 1 and 5", exceptionHigh.getMessage());

        //Score < 1
        IllegalArgumentException exceptionLow = assertThrows(IllegalArgumentException.class, () -> {
            profile.addReviewRating(0);
        });
        assertEquals("Score must be between 1 and 5", exceptionLow.getMessage());
    }
}