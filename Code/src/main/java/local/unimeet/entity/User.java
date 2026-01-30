package local.unimeet.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "app_users")
public class User {
	
	@Id
	private String username;
	private String password;
	private Role role;
	@OneToOne(mappedBy = "user", fetch = FetchType.EAGER)
    private UserProfile profile;
	
	
	//Empty constructor required by Jpa for every entity in order to create objects
	public User() {
	}
	
	public User(String username, String password, Role role) {
		
		this.username = username;
		this.password = password;
		this.role = role;
		
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public Role getRole() {
		return this.role;
	}
	
	public void setRole(Role role){
		this.role = role;
	}
	
	public UserProfile getProfile() {
		return profile;
	}

	public void setProfile(UserProfile profile) {
	    this.profile = profile;
	    if (profile != null && profile.getUser() != this) {
	        profile.setUser(this);
	    }
	}

	@Override
	public boolean equals(Object o) {
	    if (this == o) {
	        return true;
	    }
	    if (!(o instanceof User)) { 
	        return false;
	    }

	    User other = (User) o;

	    return username.equals(other.getUsername());
	}
	
	@Override
	public int hashCode() {
	    return getClass().hashCode();
	}

	public University getUniversity() {
		return this.getProfile().getUniversity();
		
	}

}
	