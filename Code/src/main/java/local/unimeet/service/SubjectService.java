package local.unimeet.service;

import java.util.List;

import org.springframework.stereotype.Service;

import local.unimeet.entity.Subject;
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
    
}
