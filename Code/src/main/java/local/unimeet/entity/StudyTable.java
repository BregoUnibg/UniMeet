package local.unimeet.entity;

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
	private int seatsNumber;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id") //creates Foreign Key column
    private Room room;
	
	public StudyTable() {
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

	public int getSeatsNumber() {
		return seatsNumber;
	}

	public void setSeatsNumber(int seatsNumber) {
		this.seatsNumber = seatsNumber;
	}

	public Room getRoom() {
		return room;
	}
	
}
