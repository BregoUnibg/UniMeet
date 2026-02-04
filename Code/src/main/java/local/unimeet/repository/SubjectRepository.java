package local.unimeet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import local.unimeet.entity.StudyCourse;
import local.unimeet.entity.Subject;
import local.unimeet.entity.University;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    // La logica per il filtro anno: prendi materie del corso con anno <= a quello scelto
    List<Subject> findByStudyCourseAndStudyYearLessThanEqual(StudyCourse course, Integer year);
    List<Subject> findByName(String name);
	List<Subject> findByStudyCourse_Department_University(University university);
}	
