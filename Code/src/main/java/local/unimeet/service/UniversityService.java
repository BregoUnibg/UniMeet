package local.unimeet.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import local.unimeet.entity.Building;
import local.unimeet.entity.Role;
import local.unimeet.entity.University;
import local.unimeet.entity.User;
import local.unimeet.repository.UniversityRepository;

@Service 
public class UniversityService {
	
	private final UniversityRepository universityRepository;

    public UniversityService(UniversityRepository universityRepository) {
        this.universityRepository = universityRepository;
    }
	
    
    @Transactional
    public University createUniversity(String name) {
        
        if (universityRepository.existsById(name)) {
            throw new IllegalArgumentException("University with name '" + name + "' already exists.");
        }

        
        University u = new University();
        u.setName(name);
        
        return universityRepository.save(u);
    }
    
    public University getUniversityByName(String name) {
        return universityRepository.findById(name)
                .orElseThrow(() -> new EntityNotFoundException("University not found: " + name));
    }
    
    public List<University> getAllUniversities() {
        return universityRepository.findAll();
    }
   
    /**
     * filtro per la griglia
     */
    public List<University> getBuildingsForUser(User user) {
    	if (user.getRole() == Role.ADMIN) {
    		// Master Admin: see everything
    		return universityRepository.findAll();
    	} 
    	else if (user.getRole() == Role.UNI_ADMIN) {
    		// Rettore: see only his unyversity
    		return universityRepository.findByName(user.getUniversity().getName());
    	}

    	return List.of(); 
    }


    public void saveUniversity(University university) {
    	if (university == null) return;

    	universityRepository.save(university);
    }


    public void deleteUniversity(University university) {
    	universityRepository.delete(university);
    }
    
}
