package local.unimeet.service;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import local.unimeet.entity.DegreeType;
import local.unimeet.entity.Department;
import local.unimeet.entity.Role;
import local.unimeet.entity.StudyCourse;
import local.unimeet.entity.User;
import local.unimeet.repository.StudyCourseRepository;

@Service
public class StudyCourseService {
	
	private final StudyCourseRepository studyCourseRepositoy;
	
	public StudyCourseService(StudyCourseRepository studyCourseRepository){
		
		this.studyCourseRepositoy = studyCourseRepository;
		
		
	}
	
	@Transactional
	public StudyCourse createStudyCourse(String name, Department department, DegreeType degreeType) {
		
		if(this.studyCourseRepositoy.findByNameAndDepartmentAndDegreeType(name, department, degreeType).isPresent()) {
			throw new IllegalArgumentException("StudyCourse'" + name + "' already exists ");
		}
		
		StudyCourse newCourse = new StudyCourse();
		newCourse.setName(name);
		newCourse.setDepartment(department);
		newCourse.setDegreeType(degreeType);
		
		return this.studyCourseRepositoy.save(newCourse);
		
	}
	
	public List<StudyCourse> getCoursesByDepartment(Department department) {
        
		if (department == null) 
        	return List.of();
        
        return studyCourseRepositoy.findByDepartment(department);
    
	}
	
	public List<StudyCourse> getCourseByDegreeType(DegreeType degreeType) {
    	
		if (degreeType == null) 
    		return List.of();
        
    	return studyCourseRepositoy.findByDegreeType(degreeType);
    	
	}
	
	@Transactional
	public void deleteStudyCourse(StudyCourse studyCourse) {		
		studyCourseRepositoy.delete(studyCourse);
	}
	
    public List<StudyCourse> getCoursesForUser(User user) {
    	
        if (user.getRole() == Role.ADMIN) 
        	return studyCourseRepositoy.findAll();
        
        else if (user.getRole() == Role.UNI_ADMIN){ 
        	
        	return studyCourseRepositoy.findAll();
             
        }
        
        return List.of();
    }

	public void saveCourse(StudyCourse course) {
		if (course == null) return;

		studyCourseRepositoy.save(course);
		
	}

	public Collection<StudyCourse> getCoursesByDepartmentAndType(Department selectedDept, DegreeType selectedType) {
		if (selectedDept == null || selectedType == null) 
        	return List.of();
        
        return studyCourseRepositoy.findByDepartmentAndDegreeType(selectedDept, selectedType);
	}
	
}
