package local.unimeet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import local.unimeet.entity.StudySession;
import local.unimeet.entity.User;

public interface StudySessionRepository extends JpaRepository <StudySession, String>{
	
	
	List<StudySession> findByOwner(User owner);
	
	//Needed to avoid LazyInitializationException
	@Query("SELECT DISTINCT s FROM StudySession s " +
	           "LEFT JOIN FETCH s.studyTable st " +
	           "LEFT JOIN FETCH st.room r " +
	           "LEFT JOIN FETCH r.building b " +
	           "LEFT JOIN FETCH b.university u " +
	           "LEFT JOIN FETCH s.owner o " +       
	           "LEFT JOIN FETCH s.partecipants p " + 
	           "WHERE s.owner.username = :username")
	List<StudySession> findByOwnerWithDetails(@Param("username") String username);
	
	@Query("SELECT DISTINCT s FROM StudySession s " +
	           "LEFT JOIN FETCH s.studyTable st " +
	           "LEFT JOIN FETCH st.room r " +
	           "LEFT JOIN FETCH r.building b " +
	           "LEFT JOIN FETCH b.university u " +
	           "LEFT JOIN FETCH s.owner o " +      
	           "LEFT JOIN FETCH s.partecipants p")
	List<StudySession> findAllWithDetails();
	
	@Query("SELECT DISTINCT s FROM StudySession s " +
		       "LEFT JOIN FETCH s.studyTable st " +
		       "LEFT JOIN FETCH st.room r " +
		       "LEFT JOIN FETCH r.building b " +
		       "LEFT JOIN FETCH b.university u " +
		       "LEFT JOIN FETCH s.owner o " +
		       "LEFT JOIN FETCH s.partecipants p " +
		       "WHERE s.id = :sessionId")
	Optional<StudySession> findSessionWithDetailsById(@Param("sessionId") Long sessionId);
	
}
