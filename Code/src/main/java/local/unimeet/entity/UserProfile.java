package local.unimeet.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    private String id;
    
    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    //--- PERSONAL DETAILS SECTION ---
    @Lob
    @Column(name = "profile_picture", columnDefinition = "BLOB")
    private byte[] profilePicture;
    
    private String firstName;
    private String lastName;
    
    public static final int length = 500;
    @Column(length = length)
    private String bio;
    
    private Double reputation;
    private Integer totVoters;

    //--- STUDY PATH SECTION ---
    @ManyToOne(fetch = FetchType.EAGER)
    private University university;

    @ManyToOne
    private Department department;

    @Enumerated(EnumType.STRING)
    private DegreeType degreeType;

    @ManyToOne
    private StudyCourse courseName;

    private Integer studyYear;

    //--- CAREER SECTION ---
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "profile_preferred_courses")
    private Set<Subject> preferredCourses = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "profile_difficult_courses")
    private Set<Subject> difficultCourses = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "profile_passed_exams")
    private Set<Subject> passedExams = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "profile_pending_exams")
    private Set<Subject> pendingExams = new HashSet<>();

    public UserProfile() {}

    //--- GETTER AND SETTER ---
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public byte[] getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(byte[] profilePicture) {
		this.profilePicture = profilePicture;
	}

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

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public Double getReputation() {
		return reputation;
	}

	public void setReputation(Double reputation) {
		this.reputation = reputation;
	}

	public Integer getTotVoters() {
		return totVoters;
	}

	public void setTotVoters(Integer totVoters) {
		this.totVoters = totVoters;
	}

	public University getUniversity() {
		return university;
	}

	public void setUniversity(University university) {
		this.university = university;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public DegreeType getDegreeType() {
		return degreeType;
	}

	public void setDegreeType(DegreeType degreeType) {
		this.degreeType = degreeType;
	}

	public StudyCourse getCourseName() {
		return courseName;
	}

	public void setCourseName(StudyCourse courseName) {
		this.courseName = courseName;
	}

	public Integer getStudyYear() {
		return studyYear;
	}

	public void setStudyYear(Integer studyYear) {
		this.studyYear = studyYear;
	}

	public Set<Subject> getPreferredCourses() {
		return preferredCourses;
	}

	public void setPreferredCourses(Set<Subject> preferredCourses) {
		this.preferredCourses = preferredCourses;
	}

	public Set<Subject> getDifficultCourses() {
		return difficultCourses;
	}

	public void setDifficultCourses(Set<Subject> difficultCourses) {
		this.difficultCourses = difficultCourses;
	}

	public Set<Subject> getPassedExams() {
		return passedExams;
	}

	public void setPassedExams(Set<Subject> passedExams) {
		this.passedExams = passedExams;
	}

	public Set<Subject> getPendingExams() {
		return pendingExams;
	}

	public void setPendingExams(Set<Subject> pendingExams) {
		this.pendingExams = pendingExams;
	}

	public static int getLength() {
		return length;
	}

	private String stringNormalization(String s) {
    	return s.substring(0, 1).toUpperCase()+s.substring(1).replace(" ", "").replace("_", " ").toLowerCase();
    }

    @PrePersist
    @PreUpdate
    private void prepareData() {
        if (firstName != null) firstName = stringNormalization(firstName.trim());
        if (lastName != null) lastName = stringNormalization(lastName.trim());
        if (bio != null) bio = bio.trim();
    }
    
    //--- EQUALS E HASHCODE ---
    //Essential for object comparison in Vaadin Binder
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
