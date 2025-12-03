package local.unimeet.security;

import com.vaadin.flow.spring.security.AuthenticationContext;
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
}