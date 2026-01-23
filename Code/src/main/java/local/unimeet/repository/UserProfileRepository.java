package local.unimeet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import local.unimeet.entity.User;
import local.unimeet.entity.UserProfile;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, String> {
    // Trova il profilo partendo dall'oggetto User
    Optional<UserProfile> findByUser(User user);
}
/*
@Repository
public interface UserProfileRepository {

	//finds a user by looking at it's username
		//Using Optional wrapper because it avoiding generating exception when no result is found and the pointer would be null:
		//The class Optional has prebuilt method that check it's content: for example .isPresent()

		//Main JpaRepository prebuilt functions:
		//save(entity)
		//findById(id)
		//findAll()
		//count()
		//deleteById(id)
		//delete(entity)
		//existesById(id) return true if found

		
		
		Optional<User> findByUsername(String username);
	
}*/
