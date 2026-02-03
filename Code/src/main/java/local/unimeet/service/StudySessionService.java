package local.unimeet.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import local.unimeet.dto.SessionSearchCriteria;
import local.unimeet.entity.SessionType;
import local.unimeet.entity.StudySession;
import local.unimeet.entity.Subject;
import local.unimeet.entity.User;
import local.unimeet.exception.StudentBusyElsewhereException;
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
	
	
	/**
	 * Saves a studysesion performing integrity checks
	 * @param studySession
	 * @return
	 */
	public StudySession saveStudySession(StudySession studySession) {
		
		//Checking for bad date time selection
		if(studySession.getStartTime().isAfter(studySession.getEndTime()) || 
				studySession.getDate().isBefore(LocalDate.now()) || 
				(studySession.getDate().isEqual(LocalDate.now()) && studySession.getStartTime().isBefore(LocalTime.now()))){
			throw new IllegalArgumentException();
		}
		
		//Checking for date overlapp
		if(!this.isTableAvailableGivenDateAndTime(studySession.getStudyTable().getId(), studySession.getDate(), studySession.getStartTime(), studySession.getEndTime())){
			throw new IllegalStateException();
		}
		
		if(!this.isUserAvailableGivenDateAndTime(studySession.getOwner().getUsername(), studySession.getDate(), studySession.getStartTime(), studySession.getEndTime())) {
			throw new StudentBusyElsewhereException();
		}
		
		
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
		
		if((studySession.getParticipants().size()+1) >= studySession.getStudyTable().getCapacity())
			throw new IllegalStateException();
		
		//User cannot participate in two sessions at the same tim, checks if user is allready in a session
		if(!this.isUserAvailableGivenDateAndTime(username, studySession.getDate(), studySession.getStartTime(), studySession.getEndTime())) {
			throw new StudentBusyElsewhereException();
		}
		
		
			
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
  
  public List <StudySession> getStudySessionsByDate(LocalDate date){
		
		return this.studySessionRepository.findByDate(date);
		
	}
	
	public List <StudySession> getStudySessionsByDateAndTableId(LocalDate date, Long tableId){
		
		return this.studySessionRepository.findByDateAndStudyTableId(date, tableId);
		
	}
	
	/**
	 * Checks for overlapping study session booked on the passed table
	 * @param table
	 */
	public boolean isTableAvailableGivenDateAndTime(Long studyTableId, LocalDate date, LocalTime startTime, LocalTime endTime) {
		
		
		ArrayList <StudySession> sessions = (ArrayList<StudySession>) this.getStudySessionsByDateAndTableId(date, studyTableId);
		
		
		for(StudySession s: sessions) {
			
			if(s.getStartTime().isBefore(endTime) && s.getEndTime().isAfter(startTime))
				return false;			
			
		}
		
		return true;
		 
	}
	
	/**
	 * Checks if a user is free 
	 */
	public boolean isUserAvailableGivenDateAndTime(String username, LocalDate date, LocalTime startTime, LocalTime endTime) {
	    
	    //Sessions where the user is Owner or Participant said date
	    List<StudySession> userSessions = studySessionRepository.findSessionsByDateAndUsername(date, username);
	    
	    for (StudySession s : userSessions) {	        
	        
	    	if (s.getStartTime().isBefore(endTime) && s.getEndTime().isAfter(startTime)) {
	            return false;
	        }
	    }
	    
	    return true;
	}
	
	public List <StudySession> getStudySessionBySubject(Subject s) {
		return studySessionRepository.findBySubject(s);
	}
	
	public List<StudySession> getStudySessionsByParticipant(String username) {
		
	    return this.studySessionRepository.findByParticipantsUsername(username);
	    
	}
	
	@Transactional 
	public List<StudySession> getSessionsFromColleagues(String username) {
	    
	    return this.studySessionRepository.findUpcomingSessionsByColleaguesAndType(username, SessionType.PUBLIC);
	}
	
	public List<StudySession> getMyScheduledSessions(String username) {
	    return studySessionRepository.findAllMyUpcomingSchedule(username);
	}
	
	public List<StudySession> getMyEndedSessions(String username) {
	    return studySessionRepository.findAllMyEndedHistory(username);
	}
	
	public List<StudySession> getUpcomingOwnedSessions(String username) {
	    return studySessionRepository.findUpcomingOwnedSessions(username);
	}
	
	public Optional<StudySession> getCurrentActiveSession(String username) {
	    List<StudySession> active = studySessionRepository.findActiveSessionsForUser(username);
	    return active.stream().findFirst();
	}
	
}
