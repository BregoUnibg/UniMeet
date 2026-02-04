package local.unimeet.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(	name = "study_table",
		uniqueConstraints = {
		@UniqueConstraint(
		columnNames = {"number", "room_id"}) 
}
)
public class StudyTable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private int number;
	private int capacity;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id") //creates Foreign Key column
    private Room room;
	
	@OneToMany(mappedBy = "studyTable")
    private List<StudySession> studySessions;
	
	public StudyTable() {
	}
	
	public StudyTable(int number, int capacity, Room room) {
		this.number = number;
		this.capacity = capacity;
		this.room = room;
	}
	
	public void setRoom(Room room) {
		this.room = room;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public Room getRoom() {
		return room;
	}

	public long getId() {
		return id;
	}
	
}
