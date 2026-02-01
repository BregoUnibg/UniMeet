package local.unimeet.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import local.unimeet.entity.StudySession;
import local.unimeet.entity.User;

@Repository
public interface StudySessionRepository extends JpaRepository <StudySession, Long>, JpaSpecificationExecutor<StudySession> {
	
	
	List<StudySession> findByOwner(User owner);
	
	List<StudySession> findByDate(LocalDate date);
	
	List<StudySession> findByDateAndStudyTableId(LocalDate date, Long studyTableId);
	
	
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
	
	
}
