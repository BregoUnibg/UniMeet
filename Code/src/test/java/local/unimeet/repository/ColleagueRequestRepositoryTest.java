package local.unimeet.repository;

import local.unimeet.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ColleagueRequestRepositoryTest {

    @Autowired
    private ColleagueRequestRepository requestRepo;

    @Autowired
    private TestEntityManager entityManager;

    private User vecchi;
    private User leggeri;
    private User bregolin;

    @BeforeEach
    void setupUsers() {
        vecchi = createUser("vecchi");
        leggeri = createUser("leggeri");
        bregolin = createUser("bregolin");
    }

    @Test
    @DisplayName("Check if request exists irrespective of sender/receiver order")
    void testExistsRequestBetween() {
        
    	createRequest(vecchi, leggeri);

        assertTrue(requestRepo.existsRequestBetween(vecchi, leggeri));
        assertTrue(requestRepo.existsRequestBetween(leggeri, vecchi));
        
        assertFalse(requestRepo.existsRequestBetween(vecchi, bregolin));
    }

    @Test
    @DisplayName("Native Query: Check if users are colleagues")
    void testAreTheyColleagues() {
        
    	vecchi.addColleague(leggeri); // This updates both sides
        
        entityManager.persist(vecchi);
        entityManager.persist(leggeri);
        entityManager.flush(); 

        assertTrue(requestRepo.areTheyColleagues("vecchi", "leggeri"));
        assertTrue(requestRepo.areTheyColleagues("leggeri", "vecchi"));
        
        assertFalse(requestRepo.areTheyColleagues("vecchi", "bregolin"));
    }

    private User createUser(String username) {
        User u = new User();
        u.setUsername(username);
        u.setPassword("pwd");
        u.setRole(Role.USER);
        entityManager.persist(u);
        return u;
    }

    private void createRequest(User from, User to) {
        ColleagueRequest req = new ColleagueRequest(from, to);
        entityManager.persist(req);
    }
}