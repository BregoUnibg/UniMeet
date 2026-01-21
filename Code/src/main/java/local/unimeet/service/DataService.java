package local.unimeet.service;

import java.util.List;

import org.springframework.stereotype.Service;

import local.unimeet.entity.DegreeType;
import local.unimeet.entity.Department;
import local.unimeet.entity.StudyCourse;
import local.unimeet.entity.Subject;
import local.unimeet.entity.University;
import local.unimeet.repository.DepartmentRepository;
import local.unimeet.repository.StudyCourseRepository;
import local.unimeet.repository.SubjectRepository;
import local.unimeet.repository.UniversityRepository;

@Service
public class DataService {

    private final UniversityRepository universityRepo;
    private final DepartmentRepository departmentRepo;
    private final StudyCourseRepository courseRepo;
    private final SubjectRepository subjectRepo;

    // Iniettiamo tutti i repository necessari
    public DataService(UniversityRepository universityRepo, DepartmentRepository departmentRepo,
                       StudyCourseRepository courseRepo, SubjectRepository subjectRepo) {
        this.universityRepo = universityRepo;
        this.departmentRepo = departmentRepo;
        this.courseRepo = courseRepo;
        this.subjectRepo = subjectRepo;
    }

    public List<University> findAllUniversities() {
        return universityRepo.findBy();
    }

    public List<Department> findDepartmentsByUniversity(University university) {
        if (university == null) return List.of();
        return departmentRepo.findByUniversity(university);
    }

    public List<StudyCourse> findCoursesByDepartment(Department department) {
        if (department == null) return List.of();
        return courseRepo.findByDepartment(department);
    }

    public List<Subject> findSubjects(StudyCourse course, Integer maxYear) {
        if (course == null || maxYear == null) return List.of();
        // Filtriamo le materie del corso che appartengono all'anno corrente o precedenti
        return subjectRepo.findByStudyCourseAndStudyYearLessThanEqual(course, maxYear);
    }
    
    public List<StudyCourse> findCourseByDegreeType(DegreeType degreeType) {
    	if (degreeType == null) return List.of();
        return courseRepo.findByDegreeType(degreeType);
	}

}
