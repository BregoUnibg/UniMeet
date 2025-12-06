package local.unimeet.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
@PageTitle("Login")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        //Background color
        getStyle().set("background-color", "#0f172a");
        
        Button registerButton = new Button("Create a new Account");
        
        registerButton.getStyle().set("margin-bottom", "20px");
        registerButton.getStyle().set("margin-top", "-25px");
        registerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        registerButton.addClickListener(e -> {
        	
        	getUI().ifPresent(ui -> ui.navigate("register"));      
        	
        });
        
        login.setAction("login");
        login.setForgotPasswordButtonVisible(false);
        login.getStyle().set("margin-bottom", "0px");
        login.getStyle().set("padding", "-5px");
        
        VerticalLayout whiteLoginBox = new VerticalLayout(login, registerButton);
        whiteLoginBox.setAlignItems(Alignment.CENTER);
        whiteLoginBox.setSpacing(false);
        whiteLoginBox.getStyle().set("background-color", "white");
        whiteLoginBox.getStyle().set("border-radius", "15px");
        whiteLoginBox.setWidth("800 px");
        
        //Logo & Title costumization
        Icon logoIcon = VaadinIcon.ACADEMY_CAP.create();
        logoIcon.setSize("4em");
        logoIcon.setColor("white");

        H1 title = new H1("UniMeet");
        title.getStyle().set("color", "white");
        title.getStyle().set("margin-top", "0");

        
        add(logoIcon, title, whiteLoginBox);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        // Gestione errore login (se password sbagliata)
        if(beforeEnterEvent.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            login.setError(true);
        }
    }
}