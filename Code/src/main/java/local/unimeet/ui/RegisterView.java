package local.unimeet.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import local.unimeet.entity.Role;
import local.unimeet.entity.User;
import local.unimeet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

@Route("register")
@PageTitle("Register")
@AnonymousAllowed // <--- CRITICAL: Allows public access
public class RegisterView extends VerticalLayout {

    private final UserService userService;

    private final TextField usernameField = new TextField("Username");
    private final PasswordField passwordField = new PasswordField("Password");
    private final PasswordField confirmPasswordField = new PasswordField("Confirm Password");
    private final Button registerButton = new Button("Register");
    private final Button loginButton = new Button("Already have an account? Login");

    @Autowired
    public RegisterView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background-color", "#0f172a");
        
        //Setting buttons widhts to match vaadin default login template
        H2 createAccountTitle = new H2("Create Account");
        
        String stdButtonWidth = "300px"; 
        
        createAccountTitle.setWidth(stdButtonWidth);
        
        usernameField.setWidth(stdButtonWidth);
        passwordField.setWidth(stdButtonWidth);
        confirmPasswordField.setWidth(stdButtonWidth);
        registerButton.setWidth(stdButtonWidth);
        
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        registerButton.getStyle().set("margin-top", "25px");        
        registerButton.getStyle().set("margin-bottom", "25px");        
        loginButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        // 1. Navigation Logic
        loginButton.addClickListener(e -> UI.getCurrent().navigate("login"));

        // 2. Registration Logic
        registerButton.addClickListener(e -> register());
        
        VerticalLayout whiteRegisterBox = new VerticalLayout(createAccountTitle, usernameField, passwordField, confirmPasswordField, registerButton, loginButton);
        whiteRegisterBox.setAlignItems(Alignment.CENTER);
        whiteRegisterBox.setSpacing(false);
        whiteRegisterBox.getStyle().set("background-color", "white");
        whiteRegisterBox.getStyle().set("border-radius", "15px");
        whiteRegisterBox.getStyle().set("padding", "45px");
        whiteRegisterBox.setWidth("800 px");
        

        //Logo & Title costumization
        Icon logoIcon = VaadinIcon.ACADEMY_CAP.create();
        logoIcon.setSize("4em");
        logoIcon.setColor("white");

        H1 titleUniMeet = new H1("UniMeet");
        titleUniMeet.getStyle().set("color", "white");
        titleUniMeet.getStyle().set("margin-top", "0");

        
        
        add(logoIcon, titleUniMeet, whiteRegisterBox);
        
        
        
    }

    private void register() {
        String username = usernameField.getValue();
        String password = passwordField.getValue();
        String confirmPassword = confirmPasswordField.getValue();

        // Basic Validation
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }
        
        // Check if user exists (Assuming your UserService has this method)
        if (userService.userExists(username)) {
            showError("Username already taken");
            return;
        }

        // Save User - Default role: USER
        userService.saveUser(new User(username, password, Role.USER));

        Notification.show("Registration successful!", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        // Redirect to login
        UI.getCurrent().navigate("login");
    }

    private void showError(String error) {
        Notification.show(error, 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}