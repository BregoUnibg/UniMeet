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
import local.unimeet.entity.StudyCourse;
import local.unimeet.entity.StudySession;
import local.unimeet.entity.StudyTable;
import local.unimeet.entity.User;
// --- AGGIUNTO IMPORT REPOSITORY ---
import local.unimeet.repository.UserRepository; 
// ----------------------------------
import local.unimeet.security.SecurityService;
import local.unimeet.service.BuildingService;
import local.unimeet.service.DataService;
import local.unimeet.service.RoomService;
import local.unimeet.service.StudySessionService;
import local.unimeet.service.StudyTableService;
import local.unimeet.service.UniversityService;
import local.unimeet.service.UserService;
import local.unimeet.ui.MainLayout;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

@Route(value = "sessions", layout = MainLayout.class)
@PageTitle("Le mie Sessioni | UniMeet")
@PermitAll
public class SessionsView extends VerticalLayout {

    private ComboBox<StudyCourse> courseSelect;
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
    private final DataService dataService;
    
  
    private final UserRepository userRepo; 
    
    private User currentUser;

    public SessionsView(UniversityService universityService, 
                        BuildingService buildingService, 
                        RoomService roomService, 
                        StudyTableService studyTableService, 
                        StudySessionService studySessionService, 
                        SecurityService securityService, 
                        UserService userService,
                        DataService dataService,
                        UserRepository userRepo) { 
       
    	this.universityService = universityService;
    	this.buildingService = buildingService;
    	this.roomService = roomService;
    	this.studyTableService = studyTableService;
    	this.studySessionService = studySessionService;
    	this.securityService = securityService;
    	this.userService = userService;
        this.dataService = dataService;
        this.userRepo = userRepo; 
    	
       
        String username = securityService.getAuthenticatedUsername();
        this.currentUser = userRepo.findById(username).orElseThrow();
       

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new H2("Manage your Sessions"));

        add(createCreationCard());

        add(new H4("Active Sessions"));
        
        VerticalLayout sessionsList = new VerticalLayout();
        sessionsList.setPadding(false);
       
        add(sessionsList);
    }
    private VerticalLayout createCreationCard() {
        VerticalLayout card = new VerticalLayout();
        card.addClassNames(LumoUtility.Background.BASE, LumoUtility.Padding.LARGE, LumoUtility.BorderRadius.LARGE, LumoUtility.BoxShadow.SMALL);
        card.setSpacing(true);

        courseSelect = new ComboBox<>("Course");
        
        // --- INIZIO DEBUG LOGIC ---
        System.out.println("🔍 [DEBUG] Inizio caricamento createCreationCard");

        if (currentUser == null) {
            System.out.println("❌ [ERRORE] currentUser è NULL! Il login non ha passato l'utente correttamente.");
        } else {
            System.out.println("👤 [DEBUG] Utente corrente: " + currentUser.getUsername());
            
            if (currentUser.getUniversity() == null) {
                System.out.println("❌ [ERRORE] L'utente " + currentUser.getUsername() + " ha getUniversity() == NULL. Verifica EAGER loading in User.java");
            } else {
                String uniName = currentUser.getUniversity().getName();
                System.out.println("🏫 [DEBUG] Università dell'utente: " + uniName);
                
                
                var corsiTrovati = dataService.getCoursesByUniversity(currentUser.getUniversity());
                
                System.out.println("📚 [DEBUG] Corsi trovati nel DB per " + uniName + ": " + corsiTrovati.size());
                
                if (corsiTrovati.isEmpty()) {
                    System.out.println("⚠️ [ATTENZIONE] La lista dei corsi è VUOTA! La tendina non si aprirà.");
                } else {
                    for (StudyCourse c : corsiTrovati) {
                       
                        String deptStatus = (c.getDepartment() != null) ? c.getDepartment().getName() : "NULL!!!";
                        System.out.println("   -> Corso: " + c.getName() + " | Dip: " + deptStatus + " | ID: " + c.getId());
                    }
                    
                    courseSelect.setItems(corsiTrovati);
                }
            }
        }
        System.out.println("🔍 [DEBUG] Fine caricamento dati");
        // --- FINE DEBUG LOGIC ---
        
      
      
        courseSelect.setItemLabelGenerator(course -> {
            String deptName = "Nessun Dipartimento";
            if (course.getDepartment() != null) {
                deptName = course.getDepartment().getName();
            }
            return course.getName() + " (" + deptName + ")";
        });
      
        
        courseSelect.setPlaceholder("Select a Course");
        courseSelect.setWidthFull();
        
        visibilitySelect = new ComboBox<>("Visibility");
        visibilitySelect.setItems(SessionType.values());
        visibilitySelect.setValue(SessionType.PUBLIC);
        visibilitySelect.setWidthFull();

        HorizontalLayout row1 = new HorizontalLayout(courseSelect, visibilitySelect);
        row1.setWidthFull();

        datePicker = new DatePicker("Date");
        datePicker.setValue(LocalDate.now());
        datePicker.setWidthFull();
        
        placeWizzardButton = new Button("Place");
        placeWizzardButton.setWidth("50%");
        
        placeWizzardButton.addClickListener(event -> {
            placeWizzard dialog = new placeWizzard(
                universityService, 
                buildingService,
                roomService,
                studyTableService,
                (StudyTable table) -> {
                    this.selectedStudyTable = table;
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
        
        startTime = new TimePicker("Start Time");
        endTime = new TimePicker("End Time"); 

       
        startTime = new TimePicker("Start Time");
        endTime = new TimePicker("End Time");
        
        startTime.setWidthFull();
        endTime.setWidthFull();
        
        startTime.setStep(Duration.ofMinutes(60));
        startTime.setValue(LocalTime.of(9, 0)); 

        endTime.setStep(Duration.ofMinutes(60));
        endTime.setValue(LocalTime.of(10, 0)); 
        
        startTime.addValueChangeListener(event -> {
            LocalTime start = event.getValue();
            LocalTime end = endTime.getValue();
            if (start != null) {
                endTime.setMin(start);
                if (end != null && end.isBefore(start)) {
                    endTime.setValue(start.plusMinutes(60)); 
                }
            }
        });

        HorizontalLayout row3 = new HorizontalLayout(startTime, endTime);
        row3.setWidthFull();
        
        description = new TextArea("Description");
        description.setPlaceholder(null);
        description.setMaxLength(500);
        description.setWidthFull();
        
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
        	
        	StudySession newStudySession = new StudySession();
        	
        	newStudySession.setCourse(this.courseSelect.getValue());
        	newStudySession.setType(this.visibilitySelect.getValue());
        	newStudySession.setDate(this.datePicker.getValue());
        	newStudySession.setStudyTable(this.selectedStudyTable);
        	newStudySession.setTimeStart(this.startTime.getValue());
        	newStudySession.setTimeEnd(this.endTime.getValue());
        	newStudySession.setDescription(this.description.getValue());
        	
        	// Anche qui, usa il currentUser caricato correttamente
        	newStudySession.setOwner(this.currentUser);
        	
        	this.studySessionService.saveStudySession(newStudySession);
        	
            Notification.show("Aggiunto con successo", 2000, Notification.Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            
            courseSelect.clear();
            datePicker.clear();
            startTime.clear();
            endTime.clear();
            placeWizzardButton.setText("Place");
            description.clear();
            selectedStudyTable = null;
            
        } else {
            Notification.show("Compila tutti i campi (incluso il luogo)", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
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