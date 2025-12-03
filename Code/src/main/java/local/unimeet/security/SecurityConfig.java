package local.unimeet.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;

import local.unimeet.ui.LoginView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
// We suppress the deprecation warning to use the stable VaadinWebSecurity class
// because the new VaadinSecurityConfigurer is currently not publicly instantiable.
@SuppressWarnings("deprecation") 
public class SecurityConfig extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	
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
  
}