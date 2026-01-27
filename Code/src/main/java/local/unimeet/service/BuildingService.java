package local.unimeet.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import local.unimeet.entity.Building;
import local.unimeet.entity.Role;
import local.unimeet.entity.University;
import local.unimeet.entity.User;
import local.unimeet.repository.BuildingRepository;
import local.unimeet.repository.UniversityRepository;

@Service
public class BuildingService {
	
	private final BuildingRepository buildingRepository;
    private final UniversityRepository universityRepository;

    public BuildingService(BuildingRepository buildingRepository, UniversityRepository universityRepository) {
        this.buildingRepository = buildingRepository;
        this.universityRepository = universityRepository;
    }
	
    
    @Transactional
    public Building createBuilding(String buildingName, String universityName) {
        
        University university = universityRepository.findById(universityName)
                .orElseThrow(() -> new EntityNotFoundException("University not found: " + universityName));

        
        if (buildingRepository.findByNameAndUniversity(buildingName, university).isPresent()) {
            throw new IllegalArgumentException("Building '" + buildingName + "' already exists at " + universityName);
        }

        
        Building newBuilding = new Building();
        newBuilding.setName(buildingName);
        newBuilding.setUniversity(university);
        
        return buildingRepository.save(newBuilding);
    }
    
    public Building getBuildingByNameAndUniversityName(String buildingName, String universityName) {
    	
        University university = universityRepository.findById(universityName)
                .orElseThrow(() -> new EntityNotFoundException("University not found: " + universityName));

    	Building building =  buildingRepository.findByNameAndUniversity(universityName, university)
    			.orElseThrow(() -> new EntityNotFoundException("Building not found: " + buildingName));
    	
    	return building;
    	
    }
    
    public Building getBuildingById(long id) {
    	
    	Building building =  buildingRepository.findById(id)
    			.orElseThrow(() -> new EntityNotFoundException("Building not found: " + id));
    	
    	return building;
    	
    }
    
    public List<Building> getBuildingsByUniversity(String universityName) {
        
        if (!universityRepository.existsById(universityName)) {
            throw new EntityNotFoundException("University not found with name : " + universityName);
        }

        return buildingRepository.findByUniversityName(universityName);
    }

   
    /**
     * filtro per la griglia
     */
    public List<Building> getBuildingsForUser(User user) {
    	if (user.getRole() == Role.ADMIN) {
    		// Master Admin: vede tutto
    		return buildingRepository.findAll();
    	} 
    	else if (user.getRole() == Role.UNI_ADMIN) {
    		// Rettore: vede solo la sua università
    		return buildingRepository.findByUniversity(user.getUniversity());
    	}

    	return List.of(); 
    }


    public void saveBuilding(Building building) {
    	if (building == null) return;

    	buildingRepository.save(building);
    }


    public void delete(Building building) {
    	buildingRepository.delete(building);
    }


}



