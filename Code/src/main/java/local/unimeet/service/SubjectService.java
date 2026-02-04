package local.unimeet.service;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import local.unimeet.entity.Role;
import local.unimeet.entity.Subject;
import local.unimeet.entity.User;
import local.unimeet.repository.SubjectRepository;

@Service
public class SubjectService {
	
	private final SubjectRepository subjectRepository;
	
	public SubjectService(SubjectRepository subjectRepository){
		
		this.subjectRepository = subjectRepository;
		
		
	}

	public List<Subject> getAllSubjects() {
		return subjectRepository.findAll();
	}
	
	/**
     * filtro per la griglia
     */
	public Collection<Subject> getSubjectForUser(User user) {
		if (user.getRole() == Role.ADMIN) {
			// Master Admin: see all
			return subjectRepository.findAll();
		} 
		else if (user.getRole() == Role.UNI_ADMIN) {
			// uni admin: see only his univeristy
			return subjectRepository.findByStudyCourse_Department_University(user.getUniversity());
		}
		
		return List.of(); 
	}

	public void deleteSubject(Subject subject) {
		subjectRepository.delete(subject);
	}

	public void saveSubject(Subject subject) {
		if (subject == null) return;

		subjectRepository.save(subject);		
	}
    
}
