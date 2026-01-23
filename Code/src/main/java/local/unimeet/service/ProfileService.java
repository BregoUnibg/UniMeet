package local.unimeet.service;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import local.unimeet.entity.User;
import local.unimeet.entity.UserProfile;
import local.unimeet.repository.UserProfileRepository;
import local.unimeet.repository.UserRepository;

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
        // 1. Carichiamo l'utente fresco dal DB per averlo nel "Persistence Context"
        User managedUser = userRepo.findById(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // 2. Controlliamo se lo User ha già il profilo (grazie al mappedBy)
        if (managedUser.getProfile() != null) {
            return managedUser.getProfile();
        }

        // 3. Se non esiste, creiamolo
        UserProfile newProfile = new UserProfile();
        
        // 4. SINCRONIZZAZIONE BI-DIREZIONALE
        // Invece di settare l'ID, settiamo la relazione su entrambi i lati
        newProfile.setUser(managedUser);
        newProfile.setReputation(2.5);
        newProfile.setTotVoters(0);

        // 5. Salviamo il profilo
        // Hibernate ora vedrà managedUser (che ha un ID valido) 
        // e userà quell'ID per il profilo grazie a @MapsId
        return profileRepo.save(newProfile);
    }
    
    @Transactional
    public void saveProfile(UserProfile profile) {
        profileRepo.save(profile); // Hibernate riconosce l'ID esistente e aggiorna la riga
    }
}
