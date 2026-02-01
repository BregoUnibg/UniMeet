package local.unimeet.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import local.unimeet.dto.SessionSearchCriteria;
import local.unimeet.entity.StudySession;
import local.unimeet.entity.User;
import local.unimeet.repository.StudySessionRepository;
import local.unimeet.repository.specifications.SearchSpecifications;

@Service
public class StudySessionService {
	
	private final StudySessionRepository studySessionRepository;
	private final UserService userService;
	
	@Autowired
	public StudySessionService(StudySessionRepository studySessionRepository, UserService userService) {
		this.studySessionRepository = studySessionRepository;
		this.userService = userService;
	}
	
	public StudySession saveStudySession(StudySession studySession) {
		
		return this.studySessionRepository.save(studySession);
		
	}
	
	public List <StudySession>getStudySessionByOwner(String username) {
		return this.studySessionRepository.findByOwnerWithDetails(username);
	}
	
	public List <StudySession>getAllStudySessions() {
		return this.studySessionRepository.findAllWithDetails();
	}
	
	
	@Transactional
	public void addPartecipant(StudySession studySession, String username){
		
		User user = userService.getUserByUsername(username);
		
		if(studySession.getParticipants().contains(user) || studySession.getOwner().equals(user))
			throw new IllegalArgumentException();
		
		studySession.addPartecipant(user);
		
		studySessionRepository.save(studySession);
		
	}
	
	@Transactional
	public void removePartecipant(StudySession studySession, String username) {
		
		User user = userService.getUserByUsername(username);
		
		if(!studySession.getParticipants().contains(user) || studySession.getOwner().equals(user))
			throw new IllegalArgumentException();
		
		studySession.removePartecipant(user);
		
		studySessionRepository.save(studySession);
		
	}
	
	public StudySession getStudySessionById(Long id) {
		
	    return studySessionRepository.findSessionWithDetailsById(id)
	            .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));
	    
	}
	
	public List<StudySession> findSessions(SessionSearchCriteria criteria) {
	    return studySessionRepository.findAll(SearchSpecifications.searchSessions(criteria));
	}
	
}
