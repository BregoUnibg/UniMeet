package local.unimeet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;

/**
 * Il punto di ingresso dell'applicazione Spring Boot.
 */
@SpringBootApplication
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        // Questa riga accende il server Tomcat e lancia l'app
        SpringApplication.run(Application.class, args);
    }
}