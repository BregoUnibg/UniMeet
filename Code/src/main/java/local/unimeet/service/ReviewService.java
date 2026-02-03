package local.unimeet.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import local.unimeet.entity.ParticipantReview;
import local.unimeet.entity.StudySession;
import local.unimeet.entity.User;
import local.unimeet.entity.UserProfile;
import local.unimeet.repository.ParticipantReviewRepository;
import local.unimeet.repository.StudySessionRepository;
import local.unimeet.repository.UserProfileRepository;
import local.unimeet.repository.UserRepository;

@Service
public class ReviewService {

    private final ParticipantReviewRepository reviewRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final StudySessionRepository sessionRepository;

    public ReviewService(ParticipantReviewRepository reviewRepository, 
                         UserProfileRepository userProfileRepository,
                         UserRepository userRepository,
                         StudySessionRepository sessionRepository) {
        this.reviewRepository = reviewRepository;
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    /**
     * Submits a review for a colleague.
     * Updates the target's reputation in UserProfile and saves a record in ParticipantReview.
     */
    @Transactional
    public void submitReview(String reviewerUsername, String targetUsername, Long sessionId, int score) {
        
        User reviewer = userRepository.findById(reviewerUsername)
                .orElseThrow(() -> new IllegalArgumentException("Reviewer not found"));
        User target = userRepository.findById(targetUsername)
                .orElseThrow(() -> new IllegalArgumentException("Target user not found"));
        StudySession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        
        
        if(!session.getParticipantsAndOwner().contains(reviewer)) {
        	throw new IllegalStateException("You did not participate in this session.");
        }
        
        if (reviewer.equals(target)) {
            throw new IllegalStateException("You cannot review yourself.");
        }
        
        
        if (reviewRepository.existsByReviewerAndTargetAndSession(reviewer, target, session)) {
            throw new IllegalStateException("You have already reviewed this user for this session.");
        }

        UserProfile targetProfile = target.getProfile();
        if (targetProfile == null) {
            throw new IllegalStateException("Target user has no profile to rate.");
        }
        
        targetProfile.addReviewRating(score);
        userProfileRepository.save(targetProfile); // Save the calculated math

        ParticipantReview historyRecord = new ParticipantReview(reviewer, target, session, score);
        reviewRepository.save(historyRecord);
    }

    /**
     * Returns a list of users in a specific session that the current user hasn't rated yet.
     */
    public List<User> getUsersToRate(Long sessionId, String currentUsername) {
        StudySession session = sessionRepository.findById(sessionId).orElseThrow();
        User currentUser = userRepository.findById(currentUsername).orElseThrow();

        // Get everyone
        List<User> participants = session.getParticipantsAndOwner();

        // Remove myself
        participants.remove(currentUser);

        participants.removeIf(target -> 
            reviewRepository.existsByReviewerAndTargetAndSession(currentUser, target, session)
        );

        return participants;
    }
}