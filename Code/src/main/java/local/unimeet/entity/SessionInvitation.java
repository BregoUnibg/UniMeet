package local.unimeet.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    private StudySession session;

    @ManyToOne
    @JoinColumn(name = "invitee_username", nullable = false)
    private User invitee;

    @Enumerated(EnumType.STRING)
    private InvitationStatus status = InvitationStatus.PENDING;

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

	public InvitationStatus getStatus() {
		return status;
	}

	public void setStatus(InvitationStatus status) {
		this.status = status;
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