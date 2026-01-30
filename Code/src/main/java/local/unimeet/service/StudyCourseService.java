package local.unimeet.service;

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
	public void deleteStudyCourse(Long id) {
		
		if (!studyCourseRepositoy.existsById(id)) {
			throw new IllegalArgumentException("Course with " + id + " does not exist.");
		}
		
		studyCourseRepositoy.deleteById(id);
	}
	
    public List<StudyCourse> getCoursesForUser(User user) {
    	
        if (user.getRole() == Role.ADMIN) 
        	return studyCourseRepositoy.findAll();
        
        else if (user.getRole() == Role.UNI_ADMIN){ 
        	
        	//POSSIBLE CRITICAL LOGIC ERROR IN WHOLE DEPARTMENT - UNIVERSITY ENTITY MANAGMENT
        	return studyCourseRepositoy.findAll();
             
        }
        
        return List.of();
    }
	
}
