package local.unimeet.dto;

import java.time.LocalDate;

import local.unimeet.entity.Building;
import local.unimeet.entity.Room;
import local.unimeet.entity.Subject;
import local.unimeet.entity.University;
import lombok.Data; // Se usi Lombok, altrimenti genera Getter/Setter

@Data
public class SessionSearchCriteria {
    private Subject subject;
    private LocalDate fromDate;
    private LocalDate toDate;
    private University university;
    private Building building;
    private Room room;
    private boolean onlyAvailable;  //StudySession with available seats
	
    public Subject getSubject() {
		return subject;
	}
	public void setSubject(Subject subject) {
		this.subject = subject;
	}
	public LocalDate getFromDate() {
		return fromDate;
	}
	public void setFromDate(LocalDate fromDate) {
		this.fromDate = fromDate;
	}
	public LocalDate getToDate() {
		return toDate;
	}
	public void setToDate(LocalDate toDate) {
		this.toDate = toDate;
	}
	public University getUniversity() {
		return university;
	}
	public void setUniversity(University university) {
		this.university = university;
	}
	public Building getBuilding() {
		return building;
	}
	public void setBuilding(Building building) {
		this.building = building;
	}
	public Room getRoom() {
		return room;
	}
	public void setRoom(Room room) {
		this.room = room;
	}
	public boolean isOnlyAvailable() {
		return onlyAvailable;
	}
	public void setOnlyAvailable(boolean onlyAvailable) {
		this.onlyAvailable = onlyAvailable;
	}
}
