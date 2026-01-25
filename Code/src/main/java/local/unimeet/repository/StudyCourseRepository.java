package local.unimeet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import local.unimeet.entity.DegreeType;
import local.unimeet.entity.Department;
import local.unimeet.entity.StudyCourse;
import local.unimeet.entity.University;

@Repository
public interface StudyCourseRepository extends JpaRepository<StudyCourse, Long> {
	List<StudyCourse> findByDepartment(Department department);

    List<StudyCourse> findByDepartment_University(University university);

   
    List<StudyCourse> findByDegreeType(DegreeType degreeType);
}
