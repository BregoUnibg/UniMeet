package local.unimeet.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import local.unimeet.entity.SessionType;
import local.unimeet.entity.StudySession;
import local.unimeet.entity.Subject;
import local.unimeet.entity.User;

@Repository
public interface StudySessionRepository extends JpaRepository <StudySession, Long>, JpaSpecificationExecutor<StudySession> {
	
	
	List<StudySession> findByOwner(User owner);
	
	List<StudySession> findByDate(LocalDate date);
	
	List<StudySession> findByDateAndStudyTableId(LocalDate date, Long studyTableId);
	
	List <StudySession> findBySubject(Subject subject);
	
	//Needed to avoid LazyInitializationException
	@Query("SELECT DISTINCT s FROM StudySession s " +
	           "LEFT JOIN FETCH s.studyTable st " +
	           "LEFT JOIN FETCH st.room r " +
	           "LEFT JOIN FETCH r.building b " +
	           "LEFT JOIN FETCH b.university u " +
	           "LEFT JOIN FETCH s.owner o " +       
	           "LEFT JOIN FETCH s.participants p " + 
	           "WHERE s.owner.username = :username")
	List<StudySession> findByOwnerWithDetails(@Param("username") String username);
	
	@Query("SELECT DISTINCT s FROM StudySession s " +
	           "LEFT JOIN FETCH s.studyTable st " +
	           "LEFT JOIN FETCH st.room r " +
	           "LEFT JOIN FETCH r.building b " +
	           "LEFT JOIN FETCH b.university u " +
	           "LEFT JOIN FETCH s.owner o " +      
	           "LEFT JOIN FETCH s.participants p")
	List<StudySession> findAllWithDetails();
	
	
	@Query("SELECT DISTINCT s FROM StudySession s " +
		       "LEFT JOIN FETCH s.studyTable st " +
		       "LEFT JOIN FETCH st.room r " +
		       "LEFT JOIN FETCH r.building b " +
		       "LEFT JOIN FETCH b.university u " +
		       "LEFT JOIN FETCH s.owner o " +
		       "LEFT JOIN FETCH s.participants p " +
		       "WHERE s.id = :sessionId")
	Optional<StudySession> findSessionWithDetailsById(@Param("sessionId") Long sessionId);

	// Finds sessions where the 'participants' list contains a User with the given 'username'
	List<StudySession> findByParticipantsUsername(String username);
	
	@Query("SELECT DISTINCT s FROM StudySession s " +
		       "LEFT JOIN s.participants p " +
		       "WHERE s.date = :date " +
		       "AND (s.owner.username = :username OR p.username = :username)")
	List<StudySession> findSessionsByDateAndUsername(@Param("date") LocalDate date, 
		                                                 @Param("username") String username);
	
	
	//Finding Sessions by status, status enum is not stored in the database but calculated
	//Therefore queries must also be run by cheking each time which sessions are in which status givent current date time
	
	@Query("SELECT s FROM StudySession s WHERE " +
           "s.date > CURRENT_DATE OR " +
           "(s.date = CURRENT_DATE AND s.startTime > CURRENT_TIME)")
    List<StudySession> findAllUpcoming();

    @Query("SELECT s FROM StudySession s WHERE " +
           "s.date = CURRENT_DATE AND " +
           "s.startTime <= CURRENT_TIME AND " +
           "s.endTime >= CURRENT_TIME")
    List<StudySession> findAllInProgress();

    @Query("SELECT s FROM StudySession s WHERE " +
           "s.date < CURRENT_DATE OR " +
           "(s.date = CURRENT_DATE AND s.endTime < CURRENT_TIME)")
    List<StudySession> findAllEnded();
	
    
    @Query("SELECT s FROM StudySession s WHERE " +
           "s.owner.username = :username AND " +
           "(s.date > CURRENT_DATE OR (s.date = CURRENT_DATE AND s.startTime > CURRENT_TIME)) " +
           "ORDER BY s.date ASC, s.startTime ASC")
    List<StudySession> findUpcomingOwnedSessions(@Param("username") String username);

    //Both joined and owned
    @Query("SELECT DISTINCT s FROM StudySession s LEFT JOIN s.participants p WHERE " +
           "(s.owner.username = :username OR p.username = :username) AND " +
           "(s.date > CURRENT_DATE OR (s.date = CURRENT_DATE AND s.startTime > CURRENT_TIME)) " +
           "ORDER BY s.date ASC, s.startTime ASC")
    List<StudySession> findAllMyUpcomingSchedule(@Param("username") String username);
    
  //Both joined and owned
    @Query("SELECT DISTINCT s FROM StudySession s LEFT JOIN s.participants p WHERE " +
           "(s.owner.username = :username OR p.username = :username) AND " +
           "(s.date < CURRENT_DATE OR (s.date = CURRENT_DATE AND s.endTime < CURRENT_TIME)) " +
           "ORDER BY s.date DESC, s.endTime DESC")
    List<StudySession> findAllMyEndedHistory(@Param("username") String username);

    
    @Query("SELECT s FROM StudySession s WHERE " +
           "s.owner IN (SELECT c FROM User u JOIN u.colleagues c WHERE u.username = :myUsername) " +
           "AND s.type = :type " + 
           "AND (s.date > CURRENT_DATE OR (s.date = CURRENT_DATE AND s.startTime > CURRENT_TIME)) " +
           "ORDER BY s.date ASC, s.startTime ASC")
    List<StudySession> findUpcomingSessionsByColleaguesAndType(
           @Param("myUsername") String myUsername, 
           @Param("type") SessionType type
    );
    
    @Query("SELECT DISTINCT s FROM StudySession s LEFT JOIN s.participants p WHERE " +
    	       "(s.owner.username = :username OR p.username = :username) AND " +
    	       "(s.date = CURRENT_DATE AND s.startTime <= CURRENT_TIME AND s.endTime >= CURRENT_TIME)")
    List<StudySession> findActiveSessionsForUser(@Param("username") String username);
    
}
