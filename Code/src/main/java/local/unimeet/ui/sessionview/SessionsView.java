package local.unimeet.ui.sessionview;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
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
import local.unimeet.exception.StudentBusyElsewhereException;
import local.unimeet.security.SecurityService;
import local.unimeet.service.BuildingService;
import local.unimeet.service.RoomService;
import local.unimeet.service.SessionInvitationService;
import local.unimeet.service.StudySessionService;
import local.unimeet.service.StudyTableService;
import local.unimeet.service.SubjectService;
import local.unimeet.service.UniversityService;
import local.unimeet.service.UserService;
import local.unimeet.ui.MainLayout;

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
    
    Details details;
    private VerticalLayout sessionCardsContainer = new VerticalLayout();
    
    private final StudySessionService studySessionService;
    private final UniversityService universityService;
	private final BuildingService buildingService;
	private final RoomService roomService;
	private final StudyTableService studyTableService;
	private final SecurityService securityService;
	private final UserService userService;
	private final SubjectService subjectService;
	private final StudySessionService sessionService;
	private final SessionInvitationService sessionInvitationService;
	
    
    public SessionsView(UniversityService universityService, BuildingService buildingService, RoomService roomService, StudyTableService studyTableService,
    					StudySessionService studySessionService, SecurityService securityService, UserService userService, SubjectService subjectService,
    					StudySessionService sessionService, SessionInvitationService sessionInvitationService) {
       
    	this.universityService = universityService;
    	this.buildingService = buildingService;
    	this.roomService = roomService;
    	this.studyTableService = studyTableService;
    	this.studySessionService = studySessionService;
    	this.securityService = securityService;
    	this.userService = userService;
    	this.subjectService = subjectService;
    	this.sessionService = sessionService;
    	this.sessionInvitationService = sessionInvitationService;
    	
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new H2("Manage your Sessions"));

        add(createCreationCard());

        add(new H4("My Active Sessions"));
        
        add(createSessionCardsContainer());
    }

	private Details createCreationCard() {
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
        LocalDate now = LocalDate.now();
        datePicker.setValue(now);
        datePicker.setMin(now);
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
        startTime.setMin(LocalTime.of(8, 0));
        startTime.setMax(LocalTime.of(21, 0));
        startTime.setValue(LocalTime.of(8, 0)); // Default start

        // Configure End Time
        endTime.setStep(Duration.ofMinutes(60));
        endTime.setMin(LocalTime.of(9, 0));
        endTime.setMax(LocalTime.of(22, 0));
        endTime.setValue(LocalTime.of(9, 0)); // Default end

        
        
        // --- THE LOGIC ---

        // 1. When Start Time changes, ensure End Time is still valid
        startTime.addValueChangeListener(event -> {
            LocalTime start = event.getValue();
            LocalTime end = endTime.getValue();
            
            if (start != null) {
                // Option A: Set min value for end time (Standard)
                // Users can't scroll to earlier times in the second picker
                endTime.setMin(start.plusMinutes(60));
                
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
        
        details = new Details(new H4("Create your session"), card);
        
	    details.setOpened(true);
	    details.setWidthFull();
        return details;
    }

    private void saveSession() {
        if (courseSelect.getValue() != null && 
        	datePicker.getValue() != null && 
        	startTime.getValue() != null && 
        	endTime.getValue() != null &&
        	selectedStudyTable != null) {
        	
        	//Creating the acutal StudySession
        	
        	StudySession newStudySession = new StudySession();
        	
        	newStudySession.setSubject(this.courseSelect.getValue());
        	newStudySession.setType(this.visibilitySelect.getValue());
        	newStudySession.setDate(this.datePicker.getValue());
        	newStudySession.setStudyTable(this.selectedStudyTable);
        	newStudySession.setStartTime(this.startTime.getValue());
        	newStudySession.setEndTime(this.endTime.getValue());
        	newStudySession.setDescription(this.description.getValue());
        	newStudySession.setOwner(this.userService.getUserByUsername(this.securityService.getAuthenticatedUsername()));
        	
        	//Saving study session to database
        	try {
        		
        		this.studySessionService.saveStudySession(newStudySession);
        	
        	}catch(Exception e) {
        		
        		if(e instanceof IllegalArgumentException) {
        			Notification.show("Bad date time Selection", 2000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
        		}
        		
        		if(e instanceof IllegalStateException) {
        			Notification.show("Selected table is already booked in selected period", 2000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
        		}
        		
        		if(e instanceof StudentBusyElsewhereException) {
        			Notification.show("You allready have a session scheduled at that time", 2000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
        		}
        		
        		
        		return;
        		
        	}
        	
            Notification.show("Added succesfully", 2000, Notification.Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            courseSelect.clear();
            visibilitySelect.setValue(SessionType.PUBLIC);
            datePicker.setValue(LocalDate.now());
            placeWizzardButton.setText("Place");
            startTime.setValue(LocalTime.of(8, 0));
            endTime.setValue(LocalTime.of(9, 0));
            description.clear();
            selectedStudyTable = null;
            
            details.setOpened(false);
            performMySessionSearch();
            
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

    private Component createSessionCardsContainer() {
    	sessionCardsContainer.setPadding(false);
        sessionCardsContainer.setSpacing(true);
        sessionCardsContainer.setWidthFull();
        sessionCardsContainer.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        
        Scroller scroller = new Scroller(sessionCardsContainer);
        scroller.setSizeFull();
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        
        performMySessionSearch();
        
		return scroller;
	}
    
    private void performMySessionSearch() {
    	String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
    	//Get the list of results from the Service
        List<StudySession> results = sessionService.getUpcomingOwnedSessions(username);

        //Clean old results
        sessionCardsContainer.removeAll();

        //Handling "Zero result"
        if (results.isEmpty()) {
            Div noResults = new Div();
            noResults.setText("You don't own any sessions.");
            sessionCardsContainer.add(noResults);
        } else {
            //SessionCard creation
            for (StudySession session : results) {
                SessionCard card = new SessionCard(session.getId(), securityService, userService, sessionService, sessionInvitationService);
                
                sessionCardsContainer.add(card);
            }
        }
    }

}