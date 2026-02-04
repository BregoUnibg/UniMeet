package local.unimeet.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;

import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
// Suppressing warnings since a deprecated method is being used
@SuppressWarnings("deprecation") 
public class SecurityConfig extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	
    	
    	 http.formLogin().successHandler((request, response, authentication) -> {
             Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

             // Se è un ADMIN o UNI_ADMIN li mandiamo alla loro dashboard 
             if (roles.contains("ROLE_ADMIN") || roles.contains("ADMIN") || 
                 roles.contains("ROLE_UNI_ADMIN") || roles.contains("UNI_ADMIN")) {
                 response.sendRedirect("/admin");
             } 
            
             else {
                 response.sendRedirect("/Home");
             }
         });
    	//Delegate to Vaadin's default security configuration
        // This handles CSRF, static resources, and internal routes automatically
        super.configure(http);
        
        //Set the custom Login View
        //If someone is not logged in he gets redirected to the login page
        setLoginView(http, LoginView.class);
    }

    @Bean
    public WebSecurityCustomizer h2ConsoleCustomizer() {
        return (web) -> web.ignoring().requestMatchers(new AntPathRequestMatcher("/h2-console/**"));
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
    	//Passwords will be encrypted with BCrypts alghoritm
    	return new BCryptPasswordEncoder();
    	
    }
  
}