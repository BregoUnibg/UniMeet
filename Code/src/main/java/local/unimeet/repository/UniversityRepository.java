package local.unimeet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import local.unimeet.entity.University;

public interface UniversityRepository extends JpaRepository<University, String>{
	
	//Standstd implementation should me enough
	
}
