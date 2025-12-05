package local.unimeet.ui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@PageTitle("Login | UniMeet")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // 1. LO SFONDO BLU SCURO (Codice esadecimale elegante)
        getStyle().set("background-color", "#0f172a");

        // 2. AZIONE DEL LOGIN (Fondamentale per farlo funzionare)
        login.setAction("login");
        login.setForgotPasswordButtonVisible(false);

        // 3. LOGO E TITOLO (Bianchi per contrastare col blu)
        Icon logoIcon = VaadinIcon.ACADEMY_CAP.create();
        logoIcon.setSize("4em");
        logoIcon.setColor("white");

        H1 title = new H1("UniMeet");
        title.getStyle().set("color", "white");
        title.getStyle().set("margin-top", "0");

        // Aggiungiamo tutto al layout
        add(logoIcon, title, login);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        // Gestione errore login (se password sbagliata)
        if(beforeEnterEvent.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            login.setError(true);
        }
    }
}