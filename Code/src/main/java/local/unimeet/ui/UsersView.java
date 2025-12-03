package local.unimeet.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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

@Route("")
@PageTitle("Users")
@PermitAll	//All authenticated users can see this page
public class UsersView extends VerticalLayout{
	
	private final UserService userService;
    private final Grid<User> grid = new Grid<>(User.class);
    
    // Form fields
    private final TextField usernameField = new TextField("Username");
    private final PasswordField passwordField = new PasswordField("Password");
    private final Button addButton = new Button("Add");
    private final Button deleteButton = new Button("Delete");

    @Autowired
    public UsersView(UserService userService) {
        this.userService = userService;

        // 1. Configure the Grid
        grid.setColumns("username", "password"); 
        
        // 2. Configure Buttons
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR); // Make it red
        deleteButton.setEnabled(false); // Disable until a row is selected

        HorizontalLayout formLayout = new HorizontalLayout(usernameField, passwordField, addButton, deleteButton);
        formLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);

        // 3. Handle Grid Selection
        // This makes the form interactive when you click a row
        grid.asSingleSelect().addValueChangeListener(event -> {
            User selectedUser = event.getValue();
            if (selectedUser != null) {
                usernameField.setValue(selectedUser.getUsername());
                passwordField.setValue(selectedUser.getPassword()); 
                deleteButton.setEnabled(true); // Enable delete
            } else {
                clearForm();
            }
        });

        // 4. Add Button Logic
        addButton.addClickListener(e -> {
            String user = usernameField.getValue();
            String pass = passwordField.getValue();
            
            if (!user.isEmpty() && !pass.isEmpty()) {
                userService.saveUser(new User(user, pass)); 
                updateGrid(); 
                clearForm();
            }
        });

        // 5. Delete Button Logic
        deleteButton.addClickListener(e -> {
            User selectedUser = grid.asSingleSelect().getValue();
            if (selectedUser != null) {
                userService.deleteUser(selectedUser.getUsername());
                updateGrid();
                clearForm();
            }
        });

        add(formLayout, grid);
        updateGrid();
    }

    private void updateGrid() {
        grid.setItems(userService.getAllUsers());
    }

    private void clearForm() {
        usernameField.clear();
        passwordField.clear();
        deleteButton.setEnabled(false);
        grid.asSingleSelect().clear();
    }

}
