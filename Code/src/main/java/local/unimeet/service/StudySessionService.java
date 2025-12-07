package local.unimeet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import local.unimeet.repository.StudySessionRepository;

@Service
public class StudySessionService {
	
	private final StudySessionRepository studySessionRepository;
	
	@Autowired
	public StudySessionService(StudySessionRepository studySessionRepository) {
		this.studySessionRepository = studySessionRepository;
	}
	
		
	
}
