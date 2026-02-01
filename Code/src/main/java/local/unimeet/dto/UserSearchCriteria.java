package local.unimeet.dto;

import local.unimeet.entity.Subject;
import local.unimeet.entity.University;
import local.unimeet.entity.StudyCourse;

public class UserSearchCriteria {
	private String firstName;       
    private String lastName;
    private String username;
    private University university;
    private StudyCourse course;
    private Subject skill;          //Find who have this subject among Preferred Courses
    private Double minReputation;
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public University getUniversity() {
		return university;
	}
	public void setUniversity(University university) {
		this.university = university;
	}
	public StudyCourse getCourse() {
		return course;
	}
	public void setCourse(StudyCourse course) {
		this.course = course;
	}
	public Subject getSkill() {
		return skill;
	}
	public void setSkill(Subject skill) {
		this.skill = skill;
	}
	public Double getMinReputation() {
		return minReputation;
	}
	public void setMinReputation(Double minReputation) {
		this.minReputation = minReputation;
	}
}
