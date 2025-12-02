package local.unimeet.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import local.unimeet.entity.User;
import local.unimeet.repository.UserRepository;

@Service
public class UserService {
	
	private final UserRepository userRepository;
	
	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	
	public User saveUser(User user) {
		return this.userRepository.save(user);
	}
	
	public void deleteUser(String username) {
		this.userRepository.deleteById(username);
	}
	
	public List<User> getAllUsers(){
		return this.userRepository.findAll();
	}
	
	
	//More methods to be implemented when needed
	
}
