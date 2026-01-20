package local.unimeet.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "app_users")
public class User {
	
	@Id
	private String username;
	private String password;
	private Role role;
	
	
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

}
	