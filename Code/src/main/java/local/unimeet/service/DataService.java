package local.unimeet.service;

import java.util.List;
import org.springframework.stereotype.Service;
import local.unimeet.entity.*;
import local.unimeet.repository.*;

@Service
public class DataService {

    private final UniversityRepository universityRepo;
    private final DepartmentRepository departmentRepo;
    private final StudyCourseRepository courseRepo;
    private final SubjectRepository subjectRepo;

    public DataService(UniversityRepository universityRepo, DepartmentRepository departmentRepo,
                       StudyCourseRepository courseRepo, SubjectRepository subjectRepo) {
        this.universityRepo = universityRepo;
        this.departmentRepo = departmentRepo;
        this.courseRepo = courseRepo;
        this.subjectRepo = subjectRepo;
    }

    
    public List<University> findAllUniversities() {
        return universityRepo.findAll(); 
    }

    public List<Department> findDepartmentsByUniversity(University university) {
        if (university == null) return List.of();
        return departmentRepo.findByUniversity(university);
    }
    public List<StudyCourse> getCoursesByUniversity(University university) {
        if (university == null) return List.of();
      
        return courseRepo.findByDepartment_University(university);
    }
    
    
    // METODI PER LA GESTINE DEI CORSI 
    public List<StudyCourse> findCoursesByDepartment(Department department) {
        if (department == null) return List.of();
        return courseRepo.findByDepartment(department);
    }
    
    public List<StudyCourse> findCourseByDegreeType(DegreeType degreeType) {
        if (degreeType == null) return List.of();
        return courseRepo.findByDegreeType(degreeType);
    }

    public List<Subject> findSubjects(StudyCourse course, Integer maxYear) {
        if (course == null || maxYear == null) return List.of();
        return subjectRepo.findByStudyCourseAndStudyYearLessThanEqual(course, maxYear);
    }

    // METODI AMMINISTRATIVI USATI DA AdminCoursesView
    public List<Department> getDepartmentsForUser(User user) {
        if (user.getRole() == Role.ADMIN) return departmentRepo.findAll();
        else if (user.getRole() == Role.UNI_ADMIN) return departmentRepo.findByUniversity(user.getUniversity());
        return List.of();
    }

    public List<Department> getDepartmentsByUniversity(University university) {
        return findDepartmentsByUniversity(university); 
    }

    public void saveDepartment(Department department) {
        if (department != null) departmentRepo.save(department);
    }

    public List<StudyCourse> getCoursesForUser(User user) {
        if (user.getRole() == Role.ADMIN) return courseRepo.findAll();
        else if (user.getRole() == Role.UNI_ADMIN) return courseRepo.findByDepartment_University(user.getUniversity());
        return List.of();
    }

    public void saveCourse(StudyCourse course) {
        if (course != null) courseRepo.save(course);
    }

    public void deleteCourse(StudyCourse course) {
        if (course != null) courseRepo.delete(course);
    }
}