package local.unimeet.security;

import com.vaadin.flow.spring.security.AuthenticationContext;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    private final AuthenticationContext authenticationContext;

    public SecurityService(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    public void logout() {
        authenticationContext.logout();
    }
    
    
    /**
     * Returns the username of the authenticated current user
     * @return username
     */
    public String getAuthenticatedUsername() {
    	
    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	
    	if(principal instanceof UserDetails) {
    		
    		String username = ((UserDetails) principal).getUsername();
    		return username;
    	}
    	
    	return null;
    }
}