package local.unimeet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import local.unimeet.entity.Building;
import local.unimeet.entity.DegreeType;
import local.unimeet.entity.Department;
import local.unimeet.entity.StudyCourse;
import local.unimeet.entity.University;

@Repository
public interface StudyCourseRepository extends JpaRepository<StudyCourse, Long> {
    // Filtra i corsi in base al dipartimento scelto
    List<StudyCourse> findByDepartment(Department department);
    
	List<StudyCourse> findByDegreeType(DegreeType degreeType);
	
	Optional<StudyCourse> findByNameAndDepartmentAndDegreeType(String name, Department department, DegreeType degreeType);
	
}
