package local.unimeet.security;

import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;
import local.unimeet.entity.User;
import local.unimeet.repository.UserRepository;

@Service
public class SecurityService {

    private final AuthenticationContext authenticationContext;
    
  
    private final UserRepository userRepository; 
 
    public SecurityService(AuthenticationContext authenticationContext, UserRepository userRepository) {
        this.authenticationContext = authenticationContext;
        this.userRepository = userRepository;
    }

    public void logout() {
        authenticationContext.logout();
    }
   
    public String getAuthenticatedUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        if(principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return username;
        }
        
        return null;
    }
    
     //Restituisce l'oggetto User completo dal database (con Ruolo e Università).
   
   
        public Optional<User> getAuthenticatedUser() {
           
            Optional<UserDetails> springUser = authenticationContext.getAuthenticatedUser(UserDetails.class);

           
            if (springUser.isPresent()) {
                String username = springUser.get().getUsername();
                
               
                return userRepository.findByUsername(username);
            }

           
            return Optional.empty();
}
}
