package local.unimeet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import local.unimeet.entity.DegreeType;
import local.unimeet.entity.Department;
import local.unimeet.entity.StudyCourse;

@Repository
public interface StudyCourseRepository extends JpaRepository<StudyCourse, Long> {
    // Filtra i corsi in base al dipartimento scelto
    List<StudyCourse> findByDepartment(Department department);

	List<StudyCourse> findByDegreeType(DegreeType degreeType);
}
