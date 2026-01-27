package local.unimeet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import local.unimeet.entity.Building;
import local.unimeet.entity.University;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long>{

	Optional<Building> findByNameAndUniversity(String name, University university);
	
	List<Building> findByUniversityName(String universityName);
	// Serve per trovare gli edifici passando direttamente l'oggetto Università del Rettore
    List<Building> findByUniversity(University university);
}
