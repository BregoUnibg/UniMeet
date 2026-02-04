package local.unimeet.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import local.unimeet.entity.Building;
import local.unimeet.entity.Department;
import local.unimeet.entity.Role;
import local.unimeet.entity.University;
import local.unimeet.entity.User;
import local.unimeet.repository.DepartmentRepository;
import local.unimeet.repository.UniversityRepository;

@Service
public class DepartmentService {

	private final DepartmentRepository departmentRepository;
	private final UniversityRepository universityRepository;
	
	public DepartmentService(DepartmentRepository departmentRepository, UniversityRepository universityRepository) {
		
		this.departmentRepository = departmentRepository;
		this.universityRepository = universityRepository;
		
	}
	
	@Transactional
	public Department createDepartment(String name, University university){
		
		if(this.departmentRepository.findByNameAndUniversity(name, university).isPresent()) {
			throw new IllegalArgumentException("Department'" + name + "' already exists ");
		}
		
		Department newDepartment = new Department();
		newDepartment.setName(name);
		newDepartment.setUniversity(university);
		
		return this.departmentRepository.save(newDepartment);
		
	}
	
	public List<Department> getDepartmentsByUniversity(University university) {
        
		if (university == null) 
        	return List.of();
        
        return departmentRepository.findByUniversity(university);
        
    }
	
	/**
	     * filtro per la griglia
	     */
	public List<Department> getDepartmentsForUser(User user) {
    	if (user.getRole() == Role.ADMIN) {
    		// Master Admin: vede tutto
    		return departmentRepository.findAll();
    	} 
    	else if (user.getRole() == Role.UNI_ADMIN) {
    		// Rettore: vede solo la sua università
    		return departmentRepository.findByUniversity(user.getUniversity());
    	}
    	
    	return List.of(); 
	}
	
	public void saveDepartment(Department department) {
    	if (department == null) return;

    	departmentRepository.save(department);
    }

    @Transactional
    public void deleteDepartment(Department departmentFromUi) {
    	Department managedDepartment = departmentRepository.findById(departmentFromUi.getId())
                .orElseThrow(() -> new RuntimeException("Building not found into DB"));
        
        University parentUniversity = managedDepartment.getUniversity();

        if (parentUniversity != null) {
            parentUniversity.removeDepartment(managedDepartment);
            
            managedDepartment.setUniversity(null);
            
            universityRepository.save(parentUniversity);
        }
        
        departmentRepository.delete(managedDepartment);
    }
	
}
