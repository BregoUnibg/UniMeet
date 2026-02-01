package local.unimeet.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import local.unimeet.entity.InvitationStatus;
import local.unimeet.entity.SessionInvitation;
import local.unimeet.entity.StudySession;
import local.unimeet.entity.User;
import local.unimeet.repository.SessionInvitationRepository;
import local.unimeet.repository.StudySessionRepository;

@Service
public class SessionInvitationService {

    private final SessionInvitationRepository invitationRepository;
    private final StudySessionRepository sessionRepository;
    
    public SessionInvitationService(SessionInvitationRepository invitationRepository, StudySessionRepository sessionRepository) {
    	this.invitationRepository = invitationRepository;
    	this.sessionRepository = sessionRepository;
    }
    
    
    @Transactional
    public void sendInvite(StudySession session, User userToInvite) {
        
        if (session.getParticipants().contains(userToInvite)) {
            throw new IllegalStateException("User is already in the session!");
        }
        if (invitationRepository.existsBySessionAndInvitee(session, userToInvite)) {
            throw new IllegalStateException("Invite already sent!");
        }

        SessionInvitation invite = new SessionInvitation();
        invite.setSession(session);
        invite.setInvitee(userToInvite);
        invite.setStatus(InvitationStatus.PENDING);
        
        invitationRepository.save(invite);
    }

    
    @Transactional
    public void acceptInvite(Long invitationId) {
        SessionInvitation invite = invitationRepository.findById(invitationId)
            .orElseThrow(() -> new IllegalArgumentException("Invite not found"));

        //Add user to the actual session
        StudySession session = invite.getSession();
        session.addPartecipant(invite.getInvitee());
        sessionRepository.save(session);

        //Close Invitation
        invite.setStatus(InvitationStatus.ACCEPTED);
        invitationRepository.save(invite);
    }

    
    public void rejectInvite(Long invitationId) {
        SessionInvitation invite = invitationRepository.findById(invitationId)
            .orElseThrow();
            
        invite.setStatus(InvitationStatus.REJECTED);
        invitationRepository.save(invite);
    }
}