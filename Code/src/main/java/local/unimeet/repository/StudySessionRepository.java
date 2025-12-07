package local.unimeet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import local.unimeet.entity.StudySession;

public interface StudySessionRepository extends JpaRepository <StudySession, String>{
	
}
