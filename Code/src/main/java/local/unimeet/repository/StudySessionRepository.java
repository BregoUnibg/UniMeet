package local.unimeet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import local.unimeet.entity.StudySession;
import local.unimeet.entity.User;

public interface StudySessionRepository extends JpaRepository <StudySession, String>{
	
	
	List<StudySession> findByOwner(User owner);
	
	//Needed to avoid LazyInitializationException
	@Query("SELECT s FROM StudySession s " +
	           "LEFT JOIN FETCH s.studyTable st " +
	           "LEFT JOIN FETCH st.room r " +
	           "LEFT JOIN FETCH r.building b " +
	           "LEFT JOIN FETCH b.university u " +
	           "WHERE s.owner.username = :username")
	List<StudySession> findByOwnerWithDetails(@Param("username") String username);
	
	@Query("SELECT s FROM StudySession s " +
	           "JOIN FETCH s.studyTable st " +
	           "JOIN FETCH st.room r " +
	           "JOIN FETCH r.building b " +
	           "JOIN FETCH b.university u")
	List<StudySession> findAllWithDetails();
}
