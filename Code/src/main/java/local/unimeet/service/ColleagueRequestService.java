package local.unimeet.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import local.unimeet.entity.ColleagueRequest;
import local.unimeet.entity.User;
import local.unimeet.repository.ColleagueRequestRepository;
import local.unimeet.repository.UserRepository;

@Service
public class ColleagueRequestService {

    private final ColleagueRequestRepository requestRepository;
    private final UserRepository userRepository;

    public ColleagueRequestService(ColleagueRequestRepository requestRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void sendRequest(User sender, User receiver) {
        
        if (sender.equals(receiver)) return;

        if (this.areColleagues(sender, receiver)) {
             throw new IllegalStateException("You are already colleagues");
        }

        if (this.requestRepository.existsRequestBetween(sender, receiver)) {
            throw new IllegalStateException("A request is already pending");
        }

        this.requestRepository.save(new ColleagueRequest(sender, receiver));
    }

    @Transactional
    public void acceptRequest(Long requestId) {
        
    	ColleagueRequest request = this.requestRepository.findById(requestId).orElseThrow();

        User sender = request.getSender();
        User receiver = request.getReceiver();

        //addColleague in sender allready updates reciver
        sender.addColleague(receiver);
        
        //Updateing database
        this.userRepository.save(sender);
        this.userRepository.save(receiver);

        //Once accepter or rejected requests are not stored at all
        this.requestRepository.delete(request);
        
    }

    @Transactional
    public void rejectRequest(Long requestId) {
    	
        this.requestRepository.deleteById(requestId);
        
    }
    
    public List<ColleagueRequest> getPendingReqeustByRevicer(User reciver){
    	
    	return this.requestRepository.findByReceiver(reciver);
    	
    }
    
    public boolean isRequestPending(User sender, User receiver) {
    
    	return this.requestRepository.existsRequestBetween(sender, receiver);
    	
    }
    
    public boolean areColleagues(User user1, User user2) {
        if (user1 == null || user2 == null) return false;
        
        return requestRepository.areTheyColleagues(user1.getUsername(), user2.getUsername());
    }
    
    @Transactional
    public void removeColleague(User u1, User u2) {
        
        User user1 = userRepository.findByUsername(u1.getUsername()).orElseThrow();
        User user2 = userRepository.findByUsername(u2.getUsername()).orElseThrow();

        if (!areColleagues(user1, user2)) {
            throw new IllegalStateException("Users are not colleagues.");
        }

        user1.removeColleague(user2);

        this.userRepository.save(user1);
        this.userRepository.save(user2);
    }
    
}