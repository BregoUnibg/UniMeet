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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ColleagueRequestServiceTest {

    @Autowired private ColleagueRequestService reqService;
    @MockBean private UserService userService;

    @Autowired private UserRepository userRepo;
    @Autowired private ColleagueRequestRepository reqRepo;

    private User u1;
    private User u2;

    @BeforeEach
    void setup() {
        u1 = new User("s.vecchi", "pass", Role.USER);
        u2 = new User("l.leggeri", "pass", Role.USER);
        userRepo.save(u1);
        userRepo.save(u2);
    }

    @Test
    @DisplayName("Send colleague request successfully")
    void testSendRequest() {
        reqService.sendRequest(u1, u2);

        assertTrue(reqService.isRequestPending(u1, u2));
        List<ColleagueRequest> pending = reqService.getPendingReqeustByRevicer(u2);
        assertEquals(1, pending.size());
    }

    @Test
    @DisplayName("Accept request establishes colleague relationship")
    void testAcceptRequest() {
        
    	reqService.sendRequest(u1, u2);
        Long reqId = reqService.getPendingReqeustByRevicer(u2).get(0).getId();

        reqService.acceptRequest(reqId);

        assertTrue(reqService.areColleagues(u1, u2));
        assertTrue(reqService.areColleagues(u2, u1)); // Bidirectional check
        
        assertFalse(reqRepo.existsById(reqId));
    }

    @Test
    @DisplayName("Fail to send request if already colleagues")
    void testSendRequest_AlreadyColleagues() {
        
    	u1.addColleague(u2);
        userRepo.save(u1);
        userRepo.save(u2);

        assertThrows(IllegalStateException.class, () -> reqService.sendRequest(u1, u2));
    }

    @Test
    @DisplayName("Remove colleague breaks relationship")
    void testRemoveColleague() {
        
    	u1.addColleague(u2);
        userRepo.save(u1);
        
        reqService.removeColleague(u1, u2);

        assertFalse(reqService.areColleagues(u1, u2));
    }
}