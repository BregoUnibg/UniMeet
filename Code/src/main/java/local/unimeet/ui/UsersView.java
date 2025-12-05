package local.unimeet.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import local.unimeet.entity.User;
import local.unimeet.service.UserService;

// --- MODIFICA QUI ---
// Cambia da "" a "users" (o "admin")
// Così non va in conflitto con la HomeView
@Route(value = "users", layout = MainLayout.class) 
@PageTitle("Gestione Utenti")
@PermitAll
public class UsersView extends VerticalLayout {

    private final UserService userService;
    private final Grid<User> grid = new Grid<>(User.class);
    private final TextField usernameField = new TextField("Username");
    private final PasswordField passwordField = new PasswordField("Password");

    public UsersView(UserService userService) {
        this.userService = userService;
        setSizeFull();

        grid.setColumns("username", "password");

        Button addBtn = new Button("Aggiungi");
        addBtn.addClickListener(e -> {
            if(!usernameField.isEmpty() && !passwordField.isEmpty()){
                userService.saveUser(new User(usernameField.getValue(), passwordField.getValue()));
                updateGrid();
            }
        });

        HorizontalLayout form = new HorizontalLayout(usernameField, passwordField, addBtn);
        form.setDefaultVerticalComponentAlignment(Alignment.BASELINE);

        add(form, grid);
        updateGrid();
    }

    private void updateGrid() {
        grid.setItems(userService.getAllUsers());
    }
}