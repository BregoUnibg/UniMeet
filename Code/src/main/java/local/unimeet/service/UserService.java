package local.unimeet.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import local.unimeet.entity.User;
import local.unimeet.repository.UserRepository;
import local.unimeet.security.SecurityConfig;

@Service
public class UserService {
	
	private final UserRepository userRepository;
	private final SecurityConfig securityConfig;
	
	
	@Autowired
	public UserService(UserRepository userRepository, SecurityConfig securityConfig) {
		this.userRepository = userRepository;
		this.securityConfig = securityConfig;
	}
	
	
	public User saveUser(User user) {
		
		String clearPassword = user.getPassword();
		user.setPassword(this.securityConfig.passwordEncoder().encode(clearPassword));
		//Crypts password before saving user
		return this.userRepository.save(user);
	}
	
	public void deleteUser(String username) {
		this.userRepository.deleteById(username);
	}
	
	public List<User> getAllUsers(){
		return this.userRepository.findAll();
	}


	public boolean userExists(String username) {
		return this.userRepository.existsById(username);
	}
	
	
	public User getUserByUsername(String username) {
		return this.userRepository.findById(username).orElse(null);
	}
	
	//More methods to be implemented when needed
	
}
