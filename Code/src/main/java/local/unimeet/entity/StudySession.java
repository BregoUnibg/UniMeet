package local.unimeet.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "study_session")
public class StudySession {
	
	//First prototype
	//I Will list strings that will be implemented as classes with //*
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String university;	//*
	private String building;	//*

	private String room;		//*
	private String address;		//* part of building
	private SessionType type;
	private CourseSubject subject;
	private String description;
	
	@Column(nullable=false)
	private LocalDate date;
	@Column(nullable=false)
	private LocalTime timeStart; 
	@Column(nullable=false)
	private LocalTime timeEnd;
	
	//!!!IMPORTANT!!!!!
	//Doing tells the database to memorize the external key of the user since it's a separate entity
	@ManyToOne
	@JoinColumn(name="owner_username", nullable=false)
	private User owner;
	
	
	//!!!!!IMPORTANT!!!!!!
	//This creates a separate tablble in the database that contains
	//session ids and usernames as it is standsrd to handle many to many realations this way
	//The join table in question is called session_partecipants
	@ManyToMany
	@JoinTable(
			  name = "session_participants", 
			  joinColumns = @JoinColumn(name = "session_id"), 
			  inverseJoinColumns = @JoinColumn(name = "username"))	
	private List <User> partecipants;
	
	
	//Empty construvtor needed by JPA
	public StudySession() {
		
	}

	
	
	public String getUniversity() {
		return university;
	}
	
	public void setUniversity(String university) {
		this.university = university;
	}
	
	public String getBuilding() {
		return building;
	}
	
	public void setBuilding(String building) {
		this.building = building;
	}
	
	public String getRoom() {
		return room;
	}
	
	public void setRoom(String room) {
		this.room = room;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public SessionType getType() {
		return type;
	}
	
	public void setType(SessionType type) {
		this.type = type;
	}
	
	public CourseSubject getSubject() {
		return subject;
	}
	
	public void setSubject(CourseSubject subject) {
		this.subject = subject;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public LocalDate getDate() {
		return date;
	}
	
	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	public LocalTime getTimeStart() {
		return timeStart;
	}
	
	public void setTimeStart(LocalTime timeStart) {
		this.timeStart = timeStart;
	}
	
	public LocalTime getTimeEnd() {
		return timeEnd;
	}
	
	public void setTimeEnd(LocalTime timeEnd) {
		this.timeEnd = timeEnd;
	}
	
	public User getOwner() {
		return owner;
	}
	
	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	public Long getId() {
		return id;
	}
	
	public List<User> getPartecipants() {
		return partecipants;
	}
	
	public void addPartecipant(User partecipant) {
		this.partecipants.add(partecipant);
	}
	
	public void removePartecipant(User partecipant) {
		this.partecipants.remove(partecipant);
	}

}
