package local.unimeet.ui.admin;

import org.springframework.dao.DataIntegrityViolationException;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.RolesAllowed;
import local.unimeet.entity.Department;
import local.unimeet.entity.Role;
import local.unimeet.entity.University;
import local.unimeet.entity.User;
import local.unimeet.security.SecurityService;
import local.unimeet.service.DepartmentService;
import local.unimeet.service.UniversityService;
import local.unimeet.service.UserService;
import local.unimeet.ui.MainLayout;

@Route(value = "admin/departments", layout = MainLayout.class)
@PageTitle("Gestione Edifici")
@RolesAllowed({"ROLE_ADMIN", "ROLE_UNI_ADMIN"})
public class AdminDepartmentsView extends VerticalLayout {

    private final DepartmentService departmentService;
    private final UniversityService universityService;
    private final User currentUser;
    private final UserService userService;
    private final SecurityService securityService;

    private Grid<Department> grid = new Grid<>(Department.class, false);
    private TextField searchField = new TextField();

    public AdminDepartmentsView(DepartmentService departmentService, UniversityService universityService,
                              SecurityService securityService, UserService userService) {
        this.departmentService = departmentService;
        this.universityService = universityService; 
        this.userService= userService;
        this.securityService=securityService;

        this.currentUser = this.userService.getUserByUsername(this.securityService.getAuthenticatedUsername());
        setSizeFull();

        configureGrid();
        
        //Bild main layout
        add(createToolbar(), createGridContainer());
        
        updateList();
    }

    private HorizontalLayout createToolbar() {
        H3 title = new H3("Depasrtments");
        title.addClassNames(LumoUtility.Margin.NONE, LumoUtility.TextColor.HEADER);

        searchField.setPlaceholder("Search depasrtments...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setWidth("500px");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> updateList());

        Button addBtn = new Button("New Depasrtment", VaadinIcon.PLUS.create());
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addBtn.addClickListener(e -> openDialog(new Department()));

        HorizontalLayout toolbar = new HorizontalLayout(title, searchField, addBtn);
        toolbar.setWidthFull();
        toolbar.addClassNames(LumoUtility.Padding.Bottom.SMALL);
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        
        title.setVisible(true); 

        return toolbar;
    }

    private Div createGridContainer() {
        Div wrapper = new Div(grid);
        wrapper.setSizeFull();
        wrapper.addClassNames(LumoUtility.Background.BASE, LumoUtility.BorderRadius.LARGE, 
                              LumoUtility.BoxShadow.SMALL, LumoUtility.Overflow.HIDDEN);
        return wrapper;
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);

        // Name Column (Bold)
        grid.addComponentColumn(b -> {
            Span name = new Span(b.getName());
            name.addClassNames(LumoUtility.FontWeight.BOLD);
            return name;
        }).setHeader("Name").setSortable(true).setComparator(Department::getName).setAutoWidth(true);

        // University Column (Badge)
        grid.addComponentColumn(b -> {
            String uniName = b.getUniversity() != null ? b.getUniversity().getName() : "-";
            Span badge = new Span(uniName);
            badge.getElement().getThemeList().add("badge contrast");
            return badge;
        })
        .setHeader("University")
        .setSortable(true)
        .setAutoWidth(true)
        .setComparator((d1, d2) -> {
            String u1 = (d1.getUniversity() != null) ? d1.getUniversity().getName() : "";
            String u2 = (d2.getUniversity() != null) ? d2.getUniversity().getName() : "";
            return String.CASE_INSENSITIVE_ORDER.compare(u1, u2);
        });

        // Actions
        grid.addComponentColumn(department -> {
            Button edit = new Button(VaadinIcon.EDIT.create());
            edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            edit.addClickListener(e -> openDialog(department));

            Button delete = new Button(VaadinIcon.TRASH.create());
            delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            delete.addClickListener(e -> {
                Dialog confirmDialog = new Dialog();
                confirmDialog.setHeaderTitle("Delete Department");
                confirmDialog.add("Are you sure to delite" + department.getName() +"? All associated study courses and subject will be deleted (if allowed).");
                Button confirmBtn = new Button("Delete", event -> {
                	try {
                        deleteDepartment(department);
                        confirmDialog.close();
                    } catch (DataIntegrityViolationException e1) {
                        // Catching DB constraint violation
                        Notification.show("CANNOT DELETE: There are dependent data associated with this university.", 
                                          5000, Notification.Position.MIDDLE)
                                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        confirmDialog.close();
                    } catch (Exception e2) {
                        // Catching generic errors
                        Notification.show("Error deleting department: " + e2.getMessage())
                                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        confirmDialog.close();
                    }
                });
                confirmBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
                Button cancelBtn = new Button("Cancel", event -> confirmDialog.close());
                confirmDialog.getFooter().add(cancelBtn, confirmBtn);
                confirmDialog.open();
            });

            HorizontalLayout actions = new HorizontalLayout(edit, delete);
            return actions;
        }).setHeader("Actions").setAutoWidth(true);
    }

    private void updateList() {	
        String filter = searchField.getValue().toLowerCase();
        grid.setItems(departmentService.getDepartmentsForUser(currentUser).stream()
            .filter(b -> b.getName().toLowerCase().contains(filter) ||
            		(b.getUniversity() != null && b.getUniversity().getName().toLowerCase().contains(filter)))
            .toList());
    }
    
    private void deleteDepartment(Department department) {
    	departmentService.deleteDepartment(department);
        Notification.show("Department deleted successfully").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        updateList();
    }

    private void openDialog	(Department department) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(department.getName() == null ? "New Department" : "Edit Department");
        dialog.setWidth("400px");
        
        TextField nameField = new TextField("Name");
        ComboBox<University> uniSelect = new ComboBox<>("University");

        nameField.setValue(department.getName() != null ? department.getName() : "");
        nameField.setWidthFull();
        uniSelect.setWidthFull();
        
        uniSelect.setItems(universityService.getAllUniversities());
        uniSelect.setItemLabelGenerator(University::getName);
        if (department.getName() == null)
        		uniSelect.setValue(currentUser.getUniversity());
        else
        	uniSelect.setValue(department.getUniversity());
        
        if(currentUser.getRole() == Role.UNI_ADMIN)
        	uniSelect.setReadOnly(true);
        
        dialog.add(nameField, uniSelect);

        Button save = new Button("Save", e -> {
            if (nameField.getValue().trim().isEmpty() || uniSelect.getValue() == null) {
                Notification.show("Please fill the entire form!"); return;
            }
            
            // Even if the UI is manipulated, we check the user rights before saving
            if (currentUser.getRole() == Role.UNI_ADMIN) {
                // Check if the selected building belongs to the admin's university
                if (!uniSelect.getValue().getName().equals(currentUser.getUniversity().getName())) {
                    Notification.show("Security Error: You cannot add department to a University you do not manage.");
                    return;
                }
            }
            
            try {
            	department.setName(nameField.getValue());
	            
	            if (currentUser.getRole() == Role.ADMIN) 
	            	department.setUniversity(uniSelect.getValue());
	            else if (currentUser.getRole() == Role.UNI_ADMIN && currentUser.getUniversity() == null) {
	            	Notification notification = new Notification("Profile is not linked to any university");
	            	notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
	            	notification.setDuration(3000);
	            	notification.setPosition(Notification.Position.BOTTOM_START);
	            	notification.open();
	            	return;
	            }
	            else
	            	department.setUniversity(currentUser.getUniversity());
	            
	            departmentService.saveDepartment(department);
	            updateList();
	            dialog.close();
	            Notification.show("Department saved successfully").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) { Notification.show("Errorer occurred while saving!"); }
        });
        
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Cancel", e -> dialog.close());
        
        dialog.getFooter().add(cancel, save);
        dialog.open();
    }
}
