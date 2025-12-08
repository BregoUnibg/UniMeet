package local.unimeet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import local.unimeet.entity.StudySession;
import local.unimeet.entity.User;

public interface StudySessionRepository extends JpaRepository <StudySession, String>{
	
	List<StudySession> findByOwner(User owner);
	
}
