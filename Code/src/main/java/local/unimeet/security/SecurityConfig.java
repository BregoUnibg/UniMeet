package local.unimeet.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import local.unimeet.ui.LoginView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Set;

@EnableWebSecurity
@Configuration
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

        super.configure(http);
        setLoginView(http, LoginView.class);
    }

    @Bean
    public WebSecurityCustomizer h2ConsoleCustomizer() {
        return (web) -> web.ignoring().requestMatchers(new AntPathRequestMatcher("/h2-console/**"));
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        
        // Usiamo BCrypt. Le password nel DB non saranno più leggibili.
        return new BCryptPasswordEncoder();
    }
}