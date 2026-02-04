package local.unimeet.ui.admin;

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
import local.unimeet.entity.DegreeType;
import local.unimeet.entity.Department;
import local.unimeet.entity.Role;
import local.unimeet.entity.StudyCourse;
import local.unimeet.entity.University;
import local.unimeet.entity.User;
import local.unimeet.security.SecurityService;
import local.unimeet.service.DepartmentService;
import local.unimeet.service.StudyCourseService;
import local.unimeet.service.UniversityService;
import local.unimeet.service.UserService;
import local.unimeet.ui.MainLayout;

@Route(value = "admin/courses", layout = MainLayout.class)
@PageTitle("Gestione Corsi e Dipartimenti")
@RolesAllowed({"ROLE_ADMIN", "ROLE_UNI_ADMIN"})
public class AdminCoursesView extends VerticalLayout {

    private final User currentUser;
    private final UserService userService;
    private final SecurityService securityService;
    private final DepartmentService departmentService;
    private final StudyCourseService studyCourseService;
    private final UniversityService universityService;
    
    private Grid<StudyCourse> grid = new Grid<>(StudyCourse.class, false);
    private TextField searchField = new TextField();

    public AdminCoursesView(SecurityService securityService, UserService userService,
    		DepartmentService departmentService, StudyCourseService studyCourseService,
    		UniversityService universityService) {
        
    	this.departmentService = departmentService;
    	this.studyCourseService = studyCourseService;
    	this.universityService = universityService;
    	this.userService=userService;
        this.securityService=securityService;
        this.currentUser = this.userService.getUserByUsername(this.securityService.getAuthenticatedUsername());


        setSizeFull();
        addClassNames("list-view", LumoUtility.Padding.MEDIUM, LumoUtility.Background.CONTRAST_5);

        configureGrid();
        add(createToolbar(), createGridContainer());
        updateList();
    }
    
    private HorizontalLayout createToolbar() {
        H3 title = new H3("Offerta Formativa");
        title.addClassNames(LumoUtility.Margin.NONE, LumoUtility.TextColor.HEADER);
        
        searchField.setPlaceholder("Find course...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setWidth("500px");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> updateList());

        Button addBtn = new Button("New Course", VaadinIcon.ACADEMY_CAP.create());
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addBtn.addClickListener(e -> openDialog(new StudyCourse()));

        HorizontalLayout toolbar = new HorizontalLayout(title, searchField, addBtn);
        toolbar.setWidthFull();
        toolbar.addClassNames(LumoUtility.Padding.Bottom.SMALL);
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
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

        grid.addComponentColumn(sc -> {
            Span name = new Span(sc.getName());
            name.getStyle().set("font-weight", "bold");
            return name;
        }).setHeader("Study Course").setSortable(true).setComparator(StudyCourse::getName).setAutoWidth(true);

        grid.addComponentColumn(sc -> {
            Span badge = new Span(sc.getDegreeType() != null ? sc.getDegreeType().name() : "-");
            if(sc.getDegreeType() == DegreeType.BACHELOR) badge.getElement().getThemeList().add("badge");
            else if(sc.getDegreeType() == DegreeType.MASTER) badge.getElement().getThemeList().add("badge success");
            else badge.getElement().getThemeList().add("badge contrast");
            return badge;
        }).setHeader("Degree Type").setSortable(true).setComparator(StudyCourse::getDegreeType).setAutoWidth(true);
        
        grid.addComponentColumn(sc -> {
            if (sc.getDepartment() != null) {
                Span depText = new Span(sc.getDepartment().getName());
                
                Span uniText = new Span(sc.getDepartment().getUniversity().getName());
                uniText.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.TextColor.SECONDARY);
                
                VerticalLayout col = new VerticalLayout(depText, uniText);
                col.setSpacing(false); col.setPadding(false);
                return col;
            }
            return new Span("-");
        })
        .setHeader("Department")
        .setAutoWidth(true)
        .setSortable(true)
        .setComparator((sc1, sc2) -> {
            String u1 = (sc1.getDepartment() != null) ? sc1.getDepartment().getName() + "" : "";
            String u2 = (sc2.getDepartment() != null) ? sc2.getDepartment().getName() + "" : "";
            return String.CASE_INSENSITIVE_ORDER.compare(u1, u2);
        });

        //Actions
        grid.addComponentColumn(course -> {
            Button edit = new Button(VaadinIcon.EDIT.create());
            edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            edit.addClickListener(e -> openDialog(course));

            Button delete = new Button(VaadinIcon.TRASH.create());
            delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            delete.addClickListener(e -> {
                Dialog confirmDialog = new Dialog();
                confirmDialog.setHeaderTitle("Delete Study Course");
                confirmDialog.add("Are you sure to delite '" + course.getName() + "'?");
                
                Button confirmBtn = new Button("Delete", event -> {
                	try {
	                    deleteStudyCourse(course);
	                    confirmDialog.close();
                	} catch (Exception e2) {
                        // Catching generic errors
                		Notification.show("CANNOT DELETE: There are active Study Sessions or dependent data associated with this university.", 
                                5000, Notification.Position.MIDDLE)
                          .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    }
                });
                confirmBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
                Button cancelBtn = new Button("Cancel", event -> confirmDialog.close());
                confirmDialog.getFooter().add(cancelBtn, confirmBtn);
                confirmDialog.open();
            });
            return new HorizontalLayout(edit, delete);
        }).setHeader("Action");
    }

    private void updateList() {
        String filter = searchField.getValue().toLowerCase();
        grid.setItems(studyCourseService.getCoursesForUser(currentUser).stream()
                .filter(c -> c.getName().toLowerCase().contains(filter) ||
                			 String.valueOf(c.getDegreeType()).toLowerCase().contains(filter) ||
                             (c.getDepartment() != null && c.getDepartment().getName().toLowerCase().contains(filter)) ||
                             (c.getDepartment() != null && c.getDepartment().getName().toLowerCase().contains(filter)))
                .toList());
    }
    
    private void deleteStudyCourse(StudyCourse table) {
    	studyCourseService.deleteStudyCourse(table);
        Notification.show("Study course deleted successfully").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        updateList();
    }

    
    private void openDialog(StudyCourse course) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(course.getId() == null ? "New Study Course" : "Edit Study Course");
        dialog.setWidth("400px");

        VerticalLayout form = new VerticalLayout();
        TextField nameField = new TextField("Course Name");
        ComboBox<DegreeType> typeSelect = new ComboBox<>("Degree Type");
        typeSelect.setItems(DegreeType.values());
        
        ComboBox<University> uniSelect = new ComboBox<>("University");
        ComboBox<Department> deptSelect = new ComboBox<>("Department");

        nameField.setWidthFull();
        typeSelect.setWidthFull(); 
        uniSelect.setWidthFull();
        deptSelect.setWidthFull();
        
        uniSelect.setItemLabelGenerator(University::getName);
        deptSelect.setItemLabelGenerator(Department::getName);

        
        uniSelect.addValueChangeListener(e -> {
            deptSelect.clear();
            if (e.getValue() != null) {
                deptSelect.setItems(departmentService.getDepartmentsByUniversity(e.getValue()));
                deptSelect.setEnabled(true);
            } else {
                deptSelect.setEnabled(false);
            }
        });

        uniSelect.setItems(universityService.getAllUniversities());
        if (course.getName() == null)
        		uniSelect.setValue(currentUser.getUniversity());
        
        if(currentUser.getRole() == Role.UNI_ADMIN)
        	uniSelect.setReadOnly(true);
        
        // Handle Edit Mode (Pre-fill existing values)
        if(course.getName() != null) {
        	nameField.setValue(course.getName());
        	if(course.getDepartment() != null) {
        		uniSelect.setValue(course.getDepartment().getUniversity());
        		deptSelect.setValue(course.getDepartment());
        		typeSelect.setValue(course.getDegreeType());
        	}
        }

        Button save = new Button("Save", e -> {
            if (deptSelect.getValue() == null || nameField.getValue().isEmpty() || typeSelect.getValue() == null) {
                Notification.show("Please fill the entire form!"); return;
            }
            
           try {
        	   course.setDepartment(deptSelect.getValue());
        	   course.setName(nameField.getValue());
        	   course.setDegreeType(typeSelect.getValue());
                
        	   studyCourseService.saveCourse(course);
                updateList();
                dialog.close();
                Notification.show("Study course saved successfully!");
            } catch (Exception ex) { Notification.show("Errorer occurred while saving!"); }
        });
        
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        form.add(uniSelect, deptSelect, typeSelect, nameField);
        dialog.add(form);
        dialog.getFooter().add(new Button("Cancel", e -> dialog.close()), save);
        dialog.open();
    }
}