package local.unimeet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import local.unimeet.entity.User;
import local.unimeet.entity.UserProfile;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, String>, JpaSpecificationExecutor<UserProfile> {
    //Find UserProfile from User object
    Optional<UserProfile> findByUser(User user);
    
    //Main JpaRepository prebuilt functions:
		//save(entity)
		//findById(id)
		//findAll()
		//count()
		//deleteById(id)
		//delete(entity)
		//existesById(id) return true if found
}
