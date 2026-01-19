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
@Table(	name = "room",
		uniqueConstraints = {
		@UniqueConstraint(
		columnNames = {"number", "building_id"}) 
}
)
public class Room {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private int number;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id") //creates Foreign Key column
    private Building building;
	
	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<StudyTable> studyTables = new ArrayList<>();
	
	public Room(){
	}
	
	public long getId() {
		return id;
	}

	public void setBuilding(Building building){
		this.building = building;
	}
	
	public Building getBuilding() {
		return building;
	}

	public void addStudyTable(StudyTable studyTable) {
		studyTables.add(studyTable);
		studyTable.setRoom(this);
	}
	
	public void removeStudyTable(StudyTable studyTable) {
		studyTables.remove(studyTable);
		studyTable.setRoom(null);
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public List<StudyTable> getStudyTables() {
		return studyTables;
	}
	
	
	
}
