package local.unimeet.ui;

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
import local.unimeet.service.DataService;
import local.unimeet.service.DepartmentService;
import local.unimeet.service.StudyCourseService;
import local.unimeet.service.UniversityService;
import local.unimeet.service.UserService;

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

    public AdminCoursesView(DataService dataService,
    		SecurityService securityService,
    		UserService userService,
    		DepartmentService departmentService,
    		StudyCourseService studyCourseService,
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

        searchField.setPlaceholder("Cerca corso o dipartimento...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> updateList());
        searchField.setWidth("300px");

        Button addDeptBtn = new Button("Nuovo Dipartimento", VaadinIcon.BUILDING.create());
        addDeptBtn.addClickListener(e -> openDepartmentDialog(new Department()));
        addDeptBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        Button addCourseBtn = new Button("Nuovo Corso", VaadinIcon.ACADEMY_CAP.create());
        addCourseBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addCourseBtn.addClickListener(e -> openCourseDialog(new StudyCourse()));

        HorizontalLayout toolbar = new HorizontalLayout(title, searchField, addDeptBtn, addCourseBtn);
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

        grid.addComponentColumn(course -> {
            Span name = new Span(course.getName());
            name.addClassNames(LumoUtility.FontWeight.BOLD);
            return name;
        }).setHeader("Corso di Laurea").setAutoWidth(true).setSortable(true);

        grid.addComponentColumn(course -> {
            Span badge = new Span(course.getDegreeType() != null ? course.getDegreeType().name() : "-");
            if(course.getDegreeType() == DegreeType.TRIENNALE) badge.getElement().getThemeList().add("badge");
            else if(course.getDegreeType() == DegreeType.MAGISTRALE) badge.getElement().getThemeList().add("badge success");
            else badge.getElement().getThemeList().add("badge contrast");
            return badge;
        }).setHeader("Livello").setAutoWidth(true);

        grid.addComponentColumn(c -> {
            Span dept = new Span(c.getDepartment() != null ? c.getDepartment().getName() : "-");
            dept.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);
            return dept;
        }).setHeader("Dipartimento").setAutoWidth(true);

        if (currentUser.getRole() == Role.ADMIN) {
            grid.addComponentColumn(c -> {
                String uniName = (c.getDepartment() != null && c.getDepartment().getUniversity() != null) 
                        ? c.getDepartment().getUniversity().getName() : "-";
                Span uniBadge = new Span(uniName);
                uniBadge.getElement().getThemeList().add("badge contrast");
                return uniBadge;
            }).setHeader("Ateneo").setAutoWidth(true);
        }

        grid.addComponentColumn(course -> {
            Button edit = new Button(VaadinIcon.EDIT.create());
            edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            edit.addClickListener(e -> openCourseDialog(course));

            Button delete = new Button(VaadinIcon.TRASH.create());
            delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            delete.addClickListener(e -> {
                Dialog confirmDialog = new Dialog();
                confirmDialog.setHeaderTitle("Elimina Corso");
                confirmDialog.add("Eliminare definitivamente '" + course.getName() + "'?");
                Button confirmBtn = new Button("Elimina", event -> {
                    
                	this.studyCourseService.deleteStudyCourse(course.getId());
                	
                    updateList();
                    Notification.show("Corso eliminato");
                    confirmDialog.close();
                });
                confirmBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
                Button cancelBtn = new Button("Annulla", event -> confirmDialog.close());
                confirmDialog.getFooter().add(cancelBtn, confirmBtn);
                confirmDialog.open();
            });
            return new HorizontalLayout(edit, delete);
        }).setHeader("Azioni");
    }

    private void updateList() {
        String filter = searchField.getValue().toLowerCase();
        grid.setItems(studyCourseService.getCoursesForUser(currentUser).stream()
                .filter(c -> c.getName().toLowerCase().contains(filter) || 
                             (c.getDepartment() != null && c.getDepartment().getName().toLowerCase().contains(filter)))
                .toList());
    }

    
    private void openCourseDialog(StudyCourse course) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(course.getId() == null ? "Nuovo Corso di Laurea" : "Modifica Corso");

        VerticalLayout form = new VerticalLayout();
        TextField nameField = new TextField("Nome Corso (es. Informatica)");
        ComboBox<DegreeType> typeSelect = new ComboBox<>("Tipo Laurea");
        typeSelect.setItems(DegreeType.values());
        
        ComboBox<University> uniSelect = new ComboBox<>("Università");
        ComboBox<Department> deptSelect = new ComboBox<>("Dipartimento");

        nameField.setWidthFull(); typeSelect.setWidthFull(); 
        uniSelect.setWidthFull(); deptSelect.setWidthFull();
        
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

        
        if (currentUser.getRole() == Role.ADMIN) {
            // Super Admin: Carica tutte le Università
            uniSelect.setItems(universityService.getAllUniversities());
            
            if (course.getDepartment() != null) {
                uniSelect.setValue(course.getDepartment().getUniversity());
                // Carichiamo i dipartimenti corrispondenti
                deptSelect.setItems(departmentService.getDepartmentsByUniversity(course.getDepartment().getUniversity()));
                deptSelect.setValue(course.getDepartment());
                deptSelect.setEnabled(true);
            } else {
                deptSelect.setEnabled(false);
            }
        } else {
           
            uniSelect.setItems(currentUser.getUniversity()); 
           
            uniSelect.setValue(currentUser.getUniversity());
          
            uniSelect.setReadOnly(true);
            
           
            deptSelect.setItems(departmentService.getDepartmentsByUniversity(currentUser.getUniversity()));
            deptSelect.setEnabled(true);
            
            if (course.getDepartment() != null) {
                deptSelect.setValue(course.getDepartment());
            }
        }

        nameField.setValue(course.getName() != null ? course.getName() : "");
        if(course.getDegreeType() != null) typeSelect.setValue(course.getDegreeType());

        Button save = new Button("Salva", e -> {
            if (deptSelect.getValue() == null || nameField.getValue().isEmpty() || typeSelect.getValue() == null) {
                Notification.show("Compila tutti i campi!"); return;
            }
            else {
	            	
	            this.studyCourseService.createStudyCourse(nameField.getValue(), deptSelect.getValue(), typeSelect.getValue());
	            
	            updateList();
	            dialog.close();
	            Notification.show("Course Saved");
	            
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        form.add(uniSelect, deptSelect, typeSelect, nameField);
        dialog.add(form);
        dialog.getFooter().add(new Button("Annulla", e -> dialog.close()), save);
        dialog.open();
    }

   
    private void openDepartmentDialog(Department department) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Nuovo Dipartimento");
        
        VerticalLayout form = new VerticalLayout();
        TextField nameField = new TextField("Nome Dipartimento");
        ComboBox<University> uniSelect = new ComboBox<>("Università");

        nameField.setWidthFull(); uniSelect.setWidthFull();
        uniSelect.setItemLabelGenerator(University::getName);

        if (currentUser.getRole() == Role.ADMIN) {
            uniSelect.setItems(universityService.getAllUniversities());
        } else {
           
            uniSelect.setItems(currentUser.getUniversity());
            uniSelect.setValue(currentUser.getUniversity());
            uniSelect.setReadOnly(true);
        }

        Button save = new Button("Crea Dipartimento", e -> {
            if (uniSelect.getValue() == null || nameField.getValue().isEmpty()) {
                Notification.show("Compila tutti i campi!"); return;
            }
            else {
            	
            	departmentService.createDepartment(nameField.getValue(), uniSelect.getValue());
            	Notification.show("Department Saved");
            	dialog.close();
            	
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        form.add(uniSelect, nameField);
        dialog.add(form);
        dialog.getFooter().add(new Button("Annulla", e -> dialog.close()), save);
        dialog.open();
    }
}