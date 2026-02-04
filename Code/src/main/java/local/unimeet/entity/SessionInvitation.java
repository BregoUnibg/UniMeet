package local.unimeet.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class SessionInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    //When deleting a session with pending invites, thoose must also be deleted
    @OnDelete(action = OnDeleteAction.CASCADE)
    private StudySession session;

    @ManyToOne
    @JoinColumn(name = "invitee_username", nullable = false)
    private User invitee;

    private LocalDateTime sentAtDateTime = LocalDateTime.now();
    
    public SessionInvitation() {
    	
    }

	public StudySession getSession() {
		return session;
	}

	public void setSession(StudySession session) {
		this.session = session;
	}

	public User getInvitee() {
		return invitee;
	}

	public void setInvitee(User invitee) {
		this.invitee = invitee;
	}

	public LocalDateTime getSentAtDateTime() {
		return sentAtDateTime;
	}

	public void setSentAtDateTime(LocalDateTime sentAtDateTime) {
		this.sentAtDateTime = sentAtDateTime;
	}

	public Long getId() {
		return id;
	}
    
    
    

}