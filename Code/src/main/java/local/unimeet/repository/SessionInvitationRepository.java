package local.unimeet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import local.unimeet.entity.InvitationStatus;
import local.unimeet.entity.SessionInvitation;
import local.unimeet.entity.StudySession;
import local.unimeet.entity.User;

public interface SessionInvitationRepository extends JpaRepository<SessionInvitation, Long> {
    
	List<SessionInvitation> findByInvitee(User invitee);
    
    List<SessionInvitation> findByInviteeAndStatus(User invitee, InvitationStatus status);
    
    boolean existsBySessionAndInvitee(StudySession session, User invitee);
    
}