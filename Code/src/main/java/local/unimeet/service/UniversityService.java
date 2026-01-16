package local.unimeet.service;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import local.unimeet.entity.University;
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
    
}
