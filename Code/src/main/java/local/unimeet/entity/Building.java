package local.unimeet.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
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
@Table(	name = "building",
		uniqueConstraints = {
		@UniqueConstraint(
		columnNames = {"name", "university_id"}) 
}
)
public class Building {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String name;
	private String address;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "university_id") //creates Foreign Key column
    private University university;

	@OneToMany(mappedBy = "building", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Room> rooms = new ArrayList<>();
	
	public Building(){
	}
		
	public long getId() {
		return id;
	}

	public void setUniversity(University university) {
		this.university = university;
	}
	
	public void addRoom(Room room) {
		rooms.add(room);
		room.setBuilding(this);
	}
	
	public void removeRoom(Room room) {
		rooms.remove(room);
		room.setBuilding(null);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public University getUniversity() {
		return university;
	}

	public List<Room> getRooms() {
		return rooms;
	}
	
	
}
