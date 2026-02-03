package local.unimeet.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import local.unimeet.entity.SessionInvitation;
import local.unimeet.entity.StudySession;
import local.unimeet.entity.User;
import local.unimeet.exception.StudentBusyElsewhereException;
import local.unimeet.repository.SessionInvitationRepository;
import local.unimeet.repository.StudySessionRepository;

@Service
public class SessionInvitationService {

    private final SessionInvitationRepository invitationRepository;
    private final StudySessionRepository sessionRepository;
    private final StudySessionService studySessionService;
    
    public SessionInvitationService(SessionInvitationRepository invitationRepository, StudySessionRepository sessionRepository, StudySessionService studySessionService) {
    	
    	this.invitationRepository = invitationRepository;
    	this.sessionRepository = sessionRepository;
    	this.studySessionService = studySessionService;
    	
    }
    
    
    @Transactional
    public void sendInvite(StudySession session, User userToInvite) {
        
        if (session.getParticipants().contains(userToInvite)) {
            throw new IllegalStateException("User is already in the session!");
        }
        
        if (invitationRepository.existsBySessionAndInvitee(session, userToInvite)) {
            throw new IllegalStateException("User allready has a pending invite!");
        }
        
        if (session.getOwner().equals(userToInvite)) {
            throw new IllegalStateException("You own the session!");
        }
        
        if(!this.studySessionService.isUserAvailableGivenDateAndTime(userToInvite.getUsername(), session.getDate(), session.getStartTime(), session.getEndTime())) {
        	throw new StudentBusyElsewhereException();
        }
        
        SessionInvitation invite = new SessionInvitation();
        invite.setSession(session);
        invite.setInvitee(userToInvite);
        
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
        this.invitationRepository.deleteById(invitationId);
    }

    
    public void rejectInvite(Long invitationId) {
        SessionInvitation invite = invitationRepository.findById(invitationId)
            .orElseThrow();
            
        invitationRepository.deleteById(invitationId);
    }
    
    /**
     * Returns a list of pending invitations for a specific user.
     */
    public List<SessionInvitation> getPendingInvitationsByInvitee(User invitee) {
        return invitationRepository.findByInvitee(invitee);
    }
}