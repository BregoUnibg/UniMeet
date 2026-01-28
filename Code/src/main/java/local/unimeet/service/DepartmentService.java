package local.unimeet.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import local.unimeet.entity.Department;
import local.unimeet.entity.University;
import local.unimeet.repository.DepartmentRepository;

@Service
public class DepartmentService {

	private final DepartmentRepository departmentRepository;
	
	public DepartmentService(DepartmentRepository departmentRepository) {
		
		this.departmentRepository = departmentRepository;
		
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
	
}
