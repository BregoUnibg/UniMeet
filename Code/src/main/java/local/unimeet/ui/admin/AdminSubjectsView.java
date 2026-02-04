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
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.RolesAllowed;
import local.unimeet.entity.DegreeType;
import local.unimeet.entity.Department;
import local.unimeet.entity.Role;
import local.unimeet.entity.StudyCourse;
import local.unimeet.entity.Subject;
import local.unimeet.entity.University;
import local.unimeet.entity.User;
import local.unimeet.security.SecurityService;
import local.unimeet.service.DepartmentService;
import local.unimeet.service.StudyCourseService;
import local.unimeet.service.SubjectService;
import local.unimeet.service.UniversityService;
import local.unimeet.service.UserService;
import local.unimeet.ui.MainLayout;

@Route(value = "admin/Subjects", layout = MainLayout.class)
@PageTitle("Manage Subjects")
@RolesAllowed({"ROLE_ADMIN", "ROLE_UNI_ADMIN"})
public class AdminSubjectsView extends VerticalLayout {

    private final SubjectService subjectService;
    private final User currentUser;
    private final UserService userService;
    private final StudyCourseService studyCourseService;
    private final SecurityService securityService;
    private final UniversityService universityService;
    private final DepartmentService departmentService;

    private Grid<Subject> grid = new Grid<>(Subject.class, false);
    private TextField searchField = new TextField();

    public AdminSubjectsView(SubjectService subjectService, UniversityService universityService, StudyCourseService studyCourseService,
    					SecurityService securityService, UserService userService, DepartmentService departmentService) {
        this.subjectService = subjectService;
        this.universityService = universityService;
        this.userService=userService;
        this.studyCourseService = studyCourseService;
        this.securityService=securityService;
        this.departmentService = departmentService;
        this.currentUser = this.userService.getUserByUsername(this.securityService.getAuthenticatedUsername());

        setSizeFull();
        addClassNames("list-view", LumoUtility.Padding.MEDIUM, LumoUtility.Background.CONTRAST_5);

        configureGrid();
        add(createToolbar(), createGridContainer());
        updateList();
    }
    
    private HorizontalLayout createToolbar() {
        H3 title = new H3("Subjects");
        title.addClassNames(LumoUtility.Margin.NONE, LumoUtility.TextColor.HEADER);

        searchField.setPlaceholder("Search subjects...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setWidth("500px");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> updateList());

        Button addBtn = new Button("New Subjects", VaadinIcon.PLUS.create());
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addBtn.addClickListener(e -> openDialog(new Subject()));

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

        // Subject Name
        grid.addColumn(new ComponentRenderer<>(subjects -> {
            Span span = new Span(subjects.getName()+"");
            span.getStyle().set("font-weight", "bold");
            return span;
        })).setHeader("Name").setSortable(true).setComparator(Subject::getName).setAutoWidth(true);
        
        // Subject yesr
        grid.addColumn(new ComponentRenderer<>(subjects -> {
            Span span = new Span(subjects.getStudyYear()+"");
            span.getStyle().set("font-weight", "bold");
            return span;
        })).setHeader("Study Year").setSortable(true).setComparator(Subject::getStudyYear).setAutoWidth(true);
        
        // Using a ComponentRenderer to stack Course Name, Department Name and University Name vertically
        grid.addColumn(new ComponentRenderer<>(subject -> {
            VerticalLayout layout = new VerticalLayout();
            layout.setPadding(false);
            layout.setSpacing(false);
            layout.setAlignItems(FlexComponent.Alignment.START);

            String studyCourseName = subject.getStudyCourse() != null ? subject.getStudyCourse().getName() : "-";
            Span studyCourseSpan = new Span(studyCourseName);

            String departmentName = "-";
            if (subject.getStudyCourse().getDepartment() != null && subject.getStudyCourse().getDepartment().getUniversity() != null) {
            	departmentName = subject.getStudyCourse().getDepartment().getName() + " - " + subject.getStudyCourse().getDepartment().getUniversity();
            }
            Span depSpan = new Span(departmentName);
            depSpan.getStyle().set("font-size", "var(--lumo-font-size-xs)"); // Smaller font
            depSpan.getStyle().set("color", "var(--lumo-secondary-text-color)"); // Lighter color

            layout.add(studyCourseSpan, depSpan);
            return layout;
        }))
        .setHeader("Study Course")
        .setSortable(true)
        .setAutoWidth(true)
        .setComparator((s1, s2) -> {
            String u1 = (s1.getStudyCourse() != null) ? s1.getStudyCourse().getName() : "";
            String u2 = (s2.getStudyCourse() != null) ? s2.getStudyCourse().getName() : "";
            return String.CASE_INSENSITIVE_ORDER.compare(u1, u2);
        });

        // Actions Column
        grid.addComponentColumn(subject -> {
            Button edit = new Button(VaadinIcon.EDIT.create());
            edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            edit.addClickListener(e -> openDialog(subject));

            Button delete = new Button(VaadinIcon.TRASH.create());
            delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            delete.addClickListener(e -> {
                Dialog confirmDialog = new Dialog();
                confirmDialog.setHeaderTitle("Delete Subject");
                confirmDialog.add("Are you sure to delite" + subject.getName() +"?");
                
                Button confirmBtn = new Button("Delete", event -> {
                	try {
	                    deleteSubject(subject);
	                    confirmDialog.close();
                	} catch (Exception e2) {
                        // Catching generic errors
                        Notification.show("Error deleting subject: " + e2.getMessage())
                                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        confirmDialog.close();
                    }
                });
                confirmBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
                Button cancelBtn = new Button("Cancel", event -> confirmDialog.close());
                confirmDialog.getFooter().add(cancelBtn, confirmBtn);
                confirmDialog.open();
            });

            return new HorizontalLayout(edit, delete);
        }).setHeader("Actions");
    }

    private void updateList() {
        String filter = searchField.getValue().toLowerCase();
        grid.setItems(subjectService.getSubjectForUser(currentUser).stream()
                .filter(s -> String.valueOf(s.getName()).contains(filter) || 
                             (s.getStudyYear() != null && (s.getStudyYear()+"").toLowerCase().contains(filter)) ||
                             (s.getStudyCourse() != null && s.getStudyCourse().getName().toLowerCase().contains(filter)) ||
                             (s.getStudyCourse().getDepartment() != null && s.getStudyCourse().getDepartment().getName().toLowerCase().contains(filter)) ||
                             (s.getStudyCourse().getDepartment().getUniversity() != null && s.getStudyCourse().getDepartment().getUniversity().getName().toLowerCase().contains(filter)))
                
                .toList());
    }
    
    private void deleteSubject(Subject subject) {
    	subjectService.deleteSubject(subject);
        Notification.show("Subject deleted successfully").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        updateList();
    }

    private void openDialog(Subject subject) {
    	Dialog dialog = new Dialog();
        dialog.setHeaderTitle(subject.getId() == null ? "New Subject" : "Edit Subject");
        dialog.setWidth("400px");

        VerticalLayout form = new VerticalLayout();
        TextField nameField = new TextField("Subject Name");
        IntegerField yearField = new IntegerField("Subject Year");
        ComboBox<DegreeType> typeSelect = new ComboBox<>("Degree Type");
        typeSelect.setItems(DegreeType.values());
        
        ComboBox<University> uniSelect = new ComboBox<>("University");
        ComboBox<Department> deptSelect = new ComboBox<>("Department");
        ComboBox<StudyCourse> courseSelect = new ComboBox<>("Study Course");

        nameField.setWidthFull();
        typeSelect.setWidthFull(); 
        uniSelect.setWidthFull();
        deptSelect.setWidthFull();
        courseSelect.setWidthFull();
        yearField.setWidthFull();
        yearField.setMin(1);
        yearField.setStep(1);
        yearField.setStepButtonsVisible(true);
        
        uniSelect.setItemLabelGenerator(University::getName);
        deptSelect.setItemLabelGenerator(Department::getName);
        courseSelect.setItemLabelGenerator(StudyCourse::getName);
        
        // --- Cascade logic (LISTENER) ---
        uniSelect.addValueChangeListener(e -> {
            deptSelect.clear();
            courseSelect.clear();
            if (e.getValue() != null) {
                deptSelect.setItems(departmentService.getDepartmentsByUniversity(e.getValue()));
                deptSelect.setEnabled(true);
            } else {
                deptSelect.setEnabled(false);
            }
        });
        
        deptSelect.addValueChangeListener(e -> {
            courseSelect.clear();
            if (e.getValue() != null) {
                // Chiama il metodo che abbiamo creato nel PASSO 2
            	updateCourseSelect(deptSelect, typeSelect, courseSelect);
            } else {
                courseSelect.setEnabled(false);
            }
        });
        
        typeSelect.addValueChangeListener(e -> {
            updateCourseSelect(deptSelect, typeSelect, courseSelect);
            switch(typeSelect.getValue()) {
            	case BACHELOR :
            		yearField.setMax(3);
            		if(yearField.getValue()>3)
            			yearField.setValue(3);
            			break;
            	case MASTER :
            		yearField.setMax(2);
            		if(yearField.getValue()>2)
            			yearField.setValue(2);
            			break;
            	case SINGLE_CICLE_MASTER :
            		yearField.setMax(5);
            		if(yearField.getValue()>5)
            			yearField.setValue(5);
            			break;
            }
            
        });

        uniSelect.setItems(universityService.getAllUniversities());
        if (subject.getName() == null)
        		uniSelect.setValue(currentUser.getUniversity());
        
        if(currentUser.getRole() == Role.UNI_ADMIN)
        	uniSelect.setReadOnly(true);
        
        if (subject.getId() != null) {
            nameField.setValue(subject.getName());
            yearField.setValue(subject.getStudyYear());
            
            // Navighiamo la gerarchia con cautela
            if (subject.getStudyCourse() != null) {
                StudyCourse course = subject.getStudyCourse();
                Department dept = course.getDepartment();
                
                if (dept != null) {
                    University uni = dept.getUniversity();
                    
                    if (uni != null) {
                        // Imposto i valori in ordine gerarchico
                        uniSelect.setValue(uni); // Questo triggera il listener dei dipartimenti
                        
                        // Riempio e seleziono il dipartimento
                        deptSelect.setItems(departmentService.getDepartmentsByUniversity(uni));
                        deptSelect.setValue(dept); // Questo triggera il listener dei corsi
                        
                        // Riempio e seleziono il corso
                        courseSelect.setItems(studyCourseService.getCoursesByDepartment(dept));
                        courseSelect.setValue(course);
                        
                        // Imposto il tipo laurea
                        typeSelect.setValue(course.getDegreeType());
                    }
                }
            }
        }

        Button save = new Button("Save", e -> {
            if (deptSelect.getValue() == null || nameField.getValue().isEmpty() || typeSelect.getValue() == null) {
                Notification.show("Please fill the entire form!"); return;
            }
            
           try {
        	   subject.setStudyCourse(courseSelect.getValue());
        	   subject.setName(nameField.getValue());
        	   subject.setStudyYear(yearField.getValue());
                
        	   subjectService.saveSubject(subject);
               updateList();
               dialog.close();
               Notification.show("Subject saved successfully!");
            } catch (Exception ex) { Notification.show("Errorer occurred while saving!"); }
        });
        
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        form.add(uniSelect, deptSelect, typeSelect, courseSelect, nameField, yearField);
        dialog.add(form);
        dialog.getFooter().add(new Button("Cancel", e -> dialog.close()), save);
        dialog.open();
    }
    
    // --- HELPER METOD ---
    private void updateCourseSelect(ComboBox<Department> deptSelect, 
            ComboBox<DegreeType> typeSelect, 
            ComboBox<StudyCourse> courseSelect) {

		Department selectedDept = deptSelect.getValue();
		DegreeType selectedType = typeSelect.getValue();
		
		if (selectedDept == null) {
			courseSelect.clear();
			courseSelect.setEnabled(false);
			return;
		}
		
		courseSelect.setEnabled(true);
		courseSelect.clear();
		
		if (selectedType != null) {
			courseSelect.setItems(studyCourseService.getCoursesByDepartmentAndType(selectedDept, selectedType));
		} else {
			courseSelect.setItems(studyCourseService.getCoursesByDepartment(selectedDept));
	}
}
}
