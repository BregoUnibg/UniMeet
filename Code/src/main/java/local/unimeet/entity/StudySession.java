package local.unimeet.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "study_session")
public class StudySession {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="study_table_id", nullable=false)
	private StudyTable studyTable;
	
	@Enumerated(EnumType.STRING)
	private SessionType type;

    // MODIFICA: Aggiunto EAGER per poter leggere il nome del corso nella Card
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "study_course_id")
    private StudyCourse course;
	
	private String description;
	
	@Column(nullable=false)
	private LocalDate date;
	@Column(nullable=false)
	private LocalTime timeStart; 
	@Column(nullable=false)
	private LocalTime timeEnd;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="owner_username", nullable=false)
	private User owner;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			  name = "session_participants", 
			  joinColumns = @JoinColumn(name = "session_id"), 
			  inverseJoinColumns = @JoinColumn(name = "username"))	
	private List <User> partecipants;
	
	public StudySession() {}

    public StudyCourse getCourse() { return course; }
    public void setCourse(StudyCourse course) { this.course = course; }

	public StudyTable getStudyTable() { return studyTable; }
	public void setStudyTable(StudyTable studyTable) { this.studyTable = studyTable; }

   
	public University getUniversity() { return this.getBuilding().getUniversity(); }
	public Building getBuilding() { return this.getRoom().getBuilding(); }
	public Room getRoom() { return this.studyTable.getRoom(); }
	public String getAddress() { return this.getBuilding().getAddress(); }

	public SessionType getType() { return type; }
	public void setType(SessionType type) { this.type = type; }
	
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	
	public LocalDate getDate() { return date; }
	public void setDate(LocalDate date) { this.date = date; }
	
	public LocalTime getTimeStart() { return timeStart; }
	public void setTimeStart(LocalTime timeStart) { this.timeStart = timeStart; }
	
	public LocalTime getTimeEnd() { return timeEnd; }
	public void setTimeEnd(LocalTime timeEnd) { this.timeEnd = timeEnd; }
	
	public User getOwner() { return owner; }
	public void setOwner(User owner) { this.owner = owner; }
	
	public Long getId() { return id; }
	
	public List<User> getPartecipants() { return partecipants; }
	
	public List<User> getPartecipantsAndOwner() {
		List<User> everybody = new ArrayList<>();
	    if (owner != null) everybody.add(owner);
	    if (partecipants != null) everybody.addAll(partecipants);
	    return everybody;
	}
	
	public void addPartecipant(User partecipant) {
		if(this.partecipants == null) this.partecipants = new ArrayList <User>();
		this.partecipants.add(partecipant);
	}
	
	public void removePartecipant(User partecipant) {
		this.partecipants.remove(partecipant);
	}
	
	public int getCountMembers(){
        if(this.partecipants == null) return 1; 
		return this.partecipants.size() + 1;
	}
}