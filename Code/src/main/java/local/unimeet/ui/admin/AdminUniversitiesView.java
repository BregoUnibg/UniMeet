package local.unimeet.ui.admin;

import org.springframework.dao.DataIntegrityViolationException;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;
import local.unimeet.entity.Building;
import local.unimeet.entity.University;
import local.unimeet.entity.User;
import local.unimeet.security.SecurityService;
import local.unimeet.service.UniversityService;
import local.unimeet.service.UserService;
import local.unimeet.ui.MainLayout;

@Route(value = "admin/universities", layout = MainLayout.class)
@PageTitle("University Management | UniMeet")
@RolesAllowed({"ADMIN", "UNI_ADMIN"})
public class AdminUniversitiesView extends VerticalLayout {

    private final UniversityService uniServer;
    
    private Grid<University> grid = new Grid<>(University.class, false);
    private TextField searchField = new TextField();
    
    private final User currentUser;

    public AdminUniversitiesView(UniversityService uniServer, SecurityService securityService, UserService userService) {
        this.uniServer = uniServer;
        this.currentUser = userService.getUserByUsername(securityService.getAuthenticatedUsername());

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        HorizontalLayout header = createHeader();
        
        configureGrid();
        
        updateList();

        add(header, grid);
    }

    private HorizontalLayout createHeader() {
        H2 title = new H2("University");
        title.getStyle().set("margin", "0");

        searchField.setPlaceholder("Find university...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> updateList());
        searchField.setWidth("500px");
        searchField.setClearButtonVisible(true);

        Button addBtn = new Button("Nuova Università", VaadinIcon.PLUS.create());
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addBtn.addClickListener(e -> openUniversityDialog(new University()));

        HorizontalLayout header = new HorizontalLayout(title, searchField, addBtn);
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        
        return header;
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);

        // Colonna Nome (Grassetto come nell'immagine Edifici)
        grid.addColumn(new ComponentRenderer<>(uni -> {
            Span span = new Span(uni.getName());
            span.getStyle().set("font-weight", "bold");
            return span;
        })).setHeader("Name").setSortable(true).setComparator(University::getName).setAutoWidth(true);

        // Action column
        grid.addComponentColumn(uni -> {
            Button editBtn = new Button(VaadinIcon.EDIT.create());
            editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            editBtn.addClickListener(e -> openUniversityDialog(uni));

            Button deleteBtn = new Button(VaadinIcon.TRASH.create());
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClickListener(e -> {
                Dialog confirmDialog = new Dialog();
                confirmDialog.setHeaderTitle("Delete University");
                confirmDialog.add("Are you sure to delete " + uni.getName() + "? All associated buildings, classrooms, and tables will be deleted.");
                Button confirmBtn = new Button("Delete");
                confirmBtn.addClickListener(e2 -> {
                	try {
                		deleteUniversity(uni);
                		confirmDialog.close();
                	} catch (DataIntegrityViolationException e3) {
                		// Catching DB constraint violation
                        Notification errorNotif = Notification.show(
                            "CANNOT DELETE: There are active Study Sessions or dependent data associated with this university.",
                            5000, 
                            Notification.Position.MIDDLE
                        );
                        errorNotif.addThemeVariants(NotificationVariant.LUMO_ERROR);
                        confirmDialog.close();

                    } catch (Exception e4) {
                        // Other generic error
                        Notification errorNotif = Notification.show(
                            "An unexpected error occurred: " + e4.getMessage(),
                            5000, 
                            Notification.Position.MIDDLE
                        );
                        errorNotif.addThemeVariants(NotificationVariant.LUMO_ERROR);
                        confirmDialog.close();
                    }
                });
                confirmBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
                Button cancelBtn = new Button("Cancel", event -> confirmDialog.close());
                confirmDialog.getFooter().add(cancelBtn, confirmBtn);
                confirmDialog.open();
            });
            return new HorizontalLayout(editBtn, deleteBtn);
        }).setHeader("Action").setAutoWidth(true);
    }
    
    private void updateList() {
        String filter = searchField.getValue().toLowerCase();
        grid.setItems(uniServer.getBuildingsForUser(currentUser).stream()
            .filter(b -> b.getName().toLowerCase().contains(filter))
            .toList());
    }

    private void deleteUniversity(University uni) {
    	uniServer.deleteUniversity(uni);
        Notification.show("University deleted successfully").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        updateList();
    }

    // --- LOGICA DIALOG (Aggiungi/Modifica) ---
    private void openUniversityDialog(University uni) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(uni.getName() == null ? "New University" : "Edit University");
        dialog.setWidth("400px");

        VerticalLayout dialogLayout = new VerticalLayout();
        TextField nameField = new TextField("Name");
        nameField.setValue(uni.getName() != null ? uni.getName() : "");
        nameField.setWidthFull();
        
        dialogLayout.add(nameField);
        dialog.add(dialogLayout);

        Button saveButton = new Button("Save", e -> {
            if (nameField.getValue().isEmpty()) {
                Notification.show("Name is mandatory");
                return;
            }
            try {
            	uni.setName(nameField.getValue());
	            uniServer.saveUniversity(uni);
	            updateList();
	            dialog.close();
	            Notification.show("University saved successfully");
            } catch (Exception ex) { Notification.show("Errorer occurred while saving!"); }
            
        });
        
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Annulla", e -> dialog.close());

        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }
}
