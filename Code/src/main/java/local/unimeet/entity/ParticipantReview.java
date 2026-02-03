package local.unimeet.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "participant_reviews", 
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_review_session", 
            columnNames = {"reviewer_username", "target_username", "session_id"}
        )
    }
)
public class ParticipantReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_username", nullable = false)
    private User reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_username", nullable = false)
    private User target;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private StudySession session;

    @Column(nullable = false)
    private int score; // 1-5

    public ParticipantReview() {}

    public ParticipantReview(User reviewer, User target, StudySession session, int score) {
        this.reviewer = reviewer;
        this.target = target;
        this.session = session;
        this.score = score;
    }

    
    public Long getId(){ 
    	return id; 
    }
    
    public User getReviewer() { 
    	return reviewer; 
    }
    
    public User getTarget() { 
    	return target; 
    }
    
    public StudySession getSession() { 
    	return session; 
    }
    
    public int getScore() { 
    	return score; 
    }
    
}