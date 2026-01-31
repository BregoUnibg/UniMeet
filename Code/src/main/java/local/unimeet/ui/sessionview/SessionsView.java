package local.unimeet.ui.sessionview;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import local.unimeet.entity.SessionType;
import local.unimeet.entity.StudySession;
import local.unimeet.entity.StudyTable;
import local.unimeet.entity.Subject;
import local.unimeet.security.SecurityService;
import local.unimeet.service.BuildingService;
import local.unimeet.service.RoomService;
import local.unimeet.service.StudySessionService;
import local.unimeet.service.StudyTableService;
import local.unimeet.service.SubjectService;
import local.unimeet.service.UniversityService;
import local.unimeet.service.UserService;
import local.unimeet.ui.MainLayout;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

//this class have the purpose of design how the section of the dashboard looks like
@Route(value = "sessions", layout = MainLayout.class)
@PageTitle("Le mie Sessioni | UniMeet")
@PermitAll
public class SessionsView extends VerticalLayout {

    
    private ComboBox<Subject> courseSelect;
    private ComboBox<SessionType> visibilitySelect;
    private DatePicker datePicker;
    private TimePicker startTime;
    private TimePicker endTime;
    private TextArea description;
    private Button placeWizzardButton;
    private StudyTable selectedStudyTable;
    
    private final StudySessionService studySessionService;
    private final UniversityService universityService;
	private final BuildingService buildingService;
	private final RoomService roomService;
	private final StudyTableService studyTableService;
	private final SecurityService securityService;
	private final UserService userService;
	private final SubjectService subjectService;
	
    
    public SessionsView(UniversityService universityService, BuildingService buildingService, RoomService roomService, StudyTableService studyTableService, StudySessionService studySessionService, SecurityService securityService, UserService userService, SubjectService subjectService) {
       
    	this.universityService = universityService;
    	this.buildingService = buildingService;
    	this.roomService = roomService;
    	this.studyTableService = studyTableService;
    	this.studySessionService = studySessionService;
    	this.securityService = securityService;
    	this.userService = userService;
    	this.subjectService = subjectService;
    	
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new H2("Manage your Sessions"));

        add(createCreationCard());

        add(new H4("Active Sessions"));
    }

    private VerticalLayout createCreationCard() {
        VerticalLayout card = new VerticalLayout();
        card.addClassNames(LumoUtility.Background.BASE, LumoUtility.Padding.LARGE, LumoUtility.BorderRadius.LARGE, LumoUtility.BoxShadow.SMALL);
        card.setSpacing(true);

        // Course & Type
        courseSelect = new ComboBox<Subject>("Course");
        courseSelect.setItems(this.subjectService.getAllSubjects());
        courseSelect.setPlaceholder("Course");
        courseSelect.setWidthFull();
        
        
        visibilitySelect = new ComboBox<>("Visibility");
        visibilitySelect.setItems(SessionType.values());
        visibilitySelect.setValue(SessionType.PUBLIC);
        visibilitySelect.setWidthFull();

        HorizontalLayout row1 = new HorizontalLayout(courseSelect, visibilitySelect);
        row1.setWidthFull();

        // Date & Place
        datePicker = new DatePicker("Date");
        datePicker.setValue(LocalDate.now());
        datePicker.setWidthFull();
        
        placeWizzardButton = new Button("Place");
        placeWizzardButton.setWidth("50%");
        
        placeWizzardButton.addClickListener(event -> {
            // Open the wizard
            placeWizzard dialog = new placeWizzard(
                universityService, 
                buildingService,
                roomService,
                studyTableService,
                
             // This is the 'onComplete' consumer logic
                (StudyTable table) -> {
                    // Update the View's state
                    this.selectedStudyTable = table;
                    
                    // Update the UI
                    String locationText = String.format("%s - %s - Room %s - Table %s",
                        table.getRoom().getBuilding().getUniversity().getName(),
                        table.getRoom().getBuilding().getName(),
                        table.getRoom().getNumber(),
                        table.getNumber()
                    );
                    placeWizzardButton.setText(locationText);
                }
                
                
            );
            
            dialog.open();
        });
        
        HorizontalLayout row2 = new HorizontalLayout(datePicker, placeWizzardButton);
        row2.setWidthFull();
        
        row2.setVerticalComponentAlignment(Alignment.END, placeWizzardButton);
        
        //PICKLING UP PLACE IDEA TO BE IMPLEMENTED
        
        //step by step wizard selection
        //modify generated code so that there is no text filed, the button originally shows "place" and then shows the selected place
        
        
        
        //Picking up start and end time
        
        startTime = new TimePicker("Start Time");
        endTime = new TimePicker("End Time");

        startTime.setWidthFull();
        endTime.setWidthFull();
        
        // Configure Start Time
        startTime.setStep(Duration.ofMinutes(60));
        startTime.setValue(LocalTime.of(9, 0)); // Default start

        // Configure End Time
        endTime.setStep(Duration.ofMinutes(60));
        endTime.setValue(LocalTime.of(10, 0)); // Default end

        
        
        // --- THE LOGIC ---

        // 1. When Start Time changes, ensure End Time is still valid
        startTime.addValueChangeListener(event -> {
            LocalTime start = event.getValue();
            LocalTime end = endTime.getValue();
            
            if (start != null) {
                // Option A: Set min value for end time (Standard)
                // Users can't scroll to earlier times in the second picker
                endTime.setMin(start);
                
                // Option B: Auto-adjust End Time if it's now invalid
                if (end != null && end.isBefore(start)) {
                    endTime.setValue(start.plusMinutes(60)); // Push end time forward automatically
                }
            }
        });

        
        
        // 2. Visually group them
        HorizontalLayout timeBracketLayout = new HorizontalLayout(startTime, endTime);
        timeBracketLayout.setAlignItems(Alignment.BASELINE); // Aligns them perfectly

        
        HorizontalLayout row3 = new HorizontalLayout(startTime, endTime);
        row3.setWidthFull();
        
        description = new TextArea("Description");
        description.setPlaceholder(null);
        description.setMaxLength(500);
        description.setWidthFull();
        
        // Tasto Salva
        Button saveBtn = new Button("Add", VaadinIcon.PLUS.create());
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveBtn.setWidthFull();
        saveBtn.addClickListener(e -> saveSession());

        card.add(row1, row2, row3, description, saveBtn);
        return card;
    }

    private void saveSession() {
        if (courseSelect.getValue() != null && 
        	datePicker.getValue() != null && 
        	startTime.getValue() != null && 
        	endTime.getValue() != null &&
        	selectedStudyTable != null) {
        	
        	//Checking for date overlapp
        	
        	if(!this.studySessionService.isTableAvailableGivenDateAndTime(selectedStudyTable.getId(), datePicker.getValue(), startTime.getValue(), endTime.getValue())) {
        		
        		Notification.show("Selected table is already booked in selected period", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
        		return;
        		
        	}
        	
        	//Creating the acutal StudySession
        	
        	StudySession newStudySession = new StudySession();
        	
        	newStudySession.setSubject(this.courseSelect.getValue());
        	newStudySession.setType(this.visibilitySelect.getValue());
        	newStudySession.setDate(this.datePicker.getValue());
        	newStudySession.setStudyTable(this.selectedStudyTable);
        	newStudySession.setTimeStart(this.startTime.getValue());
        	newStudySession.setTimeEnd(this.endTime.getValue());
        	newStudySession.setDescription(this.description.getValue());
        	newStudySession.setOwner(this.userService.getUserByUsername(this.securityService.getAuthenticatedUsername()));
        	
        	//Saving study session to database
        	this.studySessionService.saveStudySession(newStudySession);
        	
            Notification.show("Added succesfully", 2000, Notification.Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            courseSelect.clear();
            datePicker.clear();
            startTime.clear();
            endTime.clear();
            placeWizzardButton.setText("Place");
            description.clear();
            selectedStudyTable = null;
            
            
        }
        else {
            Notification.show("Please fill all fields", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    

    private void confirmDelete() {
        Dialog d = new Dialog();
        d.setHeaderTitle("Elimina Sessione");
        Button yes = new Button("Elimina", e -> {
           
            d.close();
            Notification.show("Eliminato", 2000, Notification.Position.BOTTOM_START);
        });
        yes.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        d.getFooter().add(new Button("Annulla", e -> d.close()), yes);
        d.open();
    }

}