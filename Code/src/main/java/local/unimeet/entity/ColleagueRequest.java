package local.unimeet.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ColleagueRequest {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User receiver;

    private LocalDateTime sentAtDateTime = LocalDateTime.now();
    
    
    public ColleagueRequest() {}

    public ColleagueRequest(User sender, User receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public User getReceiver() {
		return receiver;
	}

	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}

	public LocalDateTime getSentAtDateTime() {
		return sentAtDateTime;
	}

	public Long getId() {
		return id;
	}
    
    
}