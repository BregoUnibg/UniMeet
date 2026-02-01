package local.unimeet.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import local.unimeet.dto.UserSearchCriteria;
import local.unimeet.entity.User;
import local.unimeet.entity.UserProfile;
import local.unimeet.repository.UserProfileRepository;
import local.unimeet.repository.UserRepository;
import local.unimeet.repository.specifications.SearchSpecifications;

@Service
public class ProfileService {

    private final UserProfileRepository profileRepo;
    private final UserRepository userRepo;

    public ProfileService(UserProfileRepository profileRepo, UserRepository userRepo) {
        this.profileRepo = profileRepo;
        this.userRepo = userRepo;
        
    }
    
    @Transactional
    public UserProfile getOrCreateProfile(User user) {
        User managedUser = userRepo.findById(user.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (managedUser.getProfile() != null) {
            return managedUser.getProfile();
        }
        
        UserProfile newProfile = new UserProfile();
	    
        newProfile.setUser(managedUser);
        newProfile.setReputation(2.5);
        newProfile.setTotVoters(0);
        
        return profileRepo.save(newProfile);
    }
    
    @Transactional
    public void saveProfile(UserProfile profile) {
        profileRepo.save(profile);
    }
    
    public List<UserProfile> findProfiles(UserSearchCriteria criteria) {
        return profileRepo.findAll(SearchSpecifications.searchUsers(criteria));
    }
}
