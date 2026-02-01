package local.unimeet.ui;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import jakarta.annotation.security.PermitAll;
import local.unimeet.dto.SessionSearchCriteria;
import local.unimeet.dto.UserSearchCriteria;
import local.unimeet.entity.Building;
import local.unimeet.entity.Department;
import local.unimeet.entity.Room;
import local.unimeet.entity.StudyCourse;
import local.unimeet.entity.StudySession;
import local.unimeet.entity.Subject;
import local.unimeet.entity.University;
import local.unimeet.entity.UserProfile;
import local.unimeet.security.SecurityService;
import local.unimeet.service.BuildingService;
import local.unimeet.service.DepartmentService;
import local.unimeet.service.ProfileService;
import local.unimeet.service.RoomService;
import local.unimeet.service.SessionInvitationService;
import local.unimeet.service.StudyCourseService;
import local.unimeet.service.StudySessionService; // Assumi che esista
import local.unimeet.service.SubjectService;
import local.unimeet.service.UniversityService;
import local.unimeet.service.UserService;
import local.unimeet.ui.sessionview.SessionCard;

@Route(value = "search", layout = MainLayout.class)
@PageTitle("Search | UniMeet")
@PermitAll
public class SearchView extends VerticalLayout {

    private final StudySessionService sessionService;
    private final ProfileService profileService;
    private final BuildingService buildingService;
    private final UniversityService universityService;
    private final RoomService roomService;
    private final DepartmentService departmentService;
    private final StudyCourseService studyCourseService;
    private final SubjectService subjectService;
    private final SessionInvitationService sessionInvitationService;
    
    private final SecurityService securityService;
    private final UserService userService;

    Div noResults = new Div();
    //--- SESSIONS UI ---
    private VerticalLayout sessionCardsContainer = new VerticalLayout();
    private ComboBox<Subject> sessionSubject = new ComboBox<>("Subject");
    private DatePicker fromDate = new DatePicker("From");
    private DatePicker toDate = new DatePicker("To");
    private ComboBox<University> sessionUni = new ComboBox<>("University");
    private ComboBox<Building> sessionBuilding = new ComboBox<>("Building");
    private ComboBox<Room> sessionRoom = new ComboBox<>("Room");
    private Checkbox onlyAvailable = new Checkbox("Only available seats");
    Details SessionDetails;
    private Button searchSessionBtn = new Button("Find sessions", VaadinIcon.SEARCH.create());

    //--- USERS UI ---
    VerticalLayout userPage = new VerticalLayout();
    private Grid<UserProfile> userGrid = new Grid<>(UserProfile.class, false);
    private TextField firstNameFilter = new TextField("First Name");
    private TextField lastNameFilter = new TextField("Last Name");
    private TextField usernameFilter = new TextField("Username");
    private ComboBox<University> userUni = new ComboBox<>("University");
    private ComboBox<Department> userDept = new ComboBox<>("Department");
    private ComboBox<StudyCourse> userCourse = new ComboBox<>("Degree Name");
    private ComboBox<Subject> skillFilter = new ComboBox<>("Skills (Subject)");
    private NumberField minReputation = new NumberField("Min Reputation (0-5)");
    Details UserDetails;
    private Button searchUserBtn = new Button("Find users", VaadinIcon.SEARCH.create());

    public SearchView(StudySessionService sessionService, ProfileService profileService, BuildingService buildingService, UniversityService universityService,
    				  RoomService roomService, DepartmentService departmentService, StudyCourseService studyCourseService, SubjectService subjectService,
    				  SecurityService securityService, UserService userService, SessionInvitationService sessionInvitationService) {
        this.sessionService = sessionService;
        this.profileService = profileService;
        this.buildingService = buildingService;
        this.universityService = universityService;
        this.roomService = roomService;
        this.departmentService = departmentService;
        this.studyCourseService = studyCourseService;
        this.subjectService = subjectService;
        
        this.securityService = securityService;
        this.userService = userService;
        this.sessionInvitationService = sessionInvitationService;

        setSizeFull();
        setSpacing(true);
        setPadding(true);

        add(new H2("Explore UniMeet"));

        //--- TAB SISTEM ---
        Tab tabSessions = new Tab("Study sessions");
        Tab tabUsers = new Tab("Users");
        Tabs tabs = new Tabs(tabSessions, tabUsers);

        Div pages = new Div();
        pages.setSizeFull();

        VerticalLayout sessionPage = createSessionPage();
        createUserPage();
        
        //Show sessionPage on page refresh
        userPage.setVisible(false);
        pages.add(sessionPage, userPage);

        tabs.addSelectedChangeListener(event -> {
            boolean isSessionTab = event.getSelectedTab().equals(tabSessions);
            sessionPage.setVisible(isSessionTab);
            userPage.setVisible(!isSessionTab);
        });

        add(tabs, pages);
        
        //Loading common data
        loadInitialData();
        
        noResults.getStyle().set("color", "gray").set("font-style", "italic").set("margin-top", "20px");
    }

    // ==========================================
    // SESSIONS SECTION
    // ==========================================
    private VerticalLayout createSessionPage() {
    	//Perform the data's logic
        LocalDate today = LocalDate.now();
        fromDate.setMin(today);
        fromDate.setValue(today);
        toDate.setMin(today);
        
        fromDate.addValueChangeListener(e -> {
            LocalDate selectedFrom = e.getValue();

            if (selectedFrom != null) {
                //A: If the user has chosen a "From" date, the "To" date must start from there
                toDate.setMin(selectedFrom);

                // B: Consistency check
                // If the user selected a "From" date, the "To" date must start from there. If the user had already selected a "To" date that is now
                // before the new "From" date, we delete it to avoid logical errors.
                if (toDate.getValue() != null && toDate.getValue().isBefore(selectedFrom)) {
                    toDate.clear();
                }
            } else {
                // C: If the user deletes the "From" date, the "To" date returns to having
            	// the only constraint being "Today" (not the past)
                toDate.setMin(today);
            }
        });
        
        toDate.addValueChangeListener(e -> {
            LocalDate selectedTo = e.getValue();
            if (selectedTo != null) {
                fromDate.setMax(selectedTo);
            } else {
                fromDate.setMax(null);
            }
        });
    	
    	//Set what to see in the ComboBoxes
    	sessionBuilding.setItemLabelGenerator(Building::getName);
    	sessionRoom.setItemLabelGenerator(room -> String.valueOf(room.getNumber()));
    	sessionUni.setItemLabelGenerator(University::getName);
    	
        //Filters configuration
        sessionBuilding.setEnabled(false);
        sessionRoom.setEnabled(false);
        
        //Waterfall logic (University -> Building -> Room)
        sessionUni.addValueChangeListener(e -> {
            sessionBuilding.clear();
            sessionRoom.clear();
            sessionRoom.setEnabled(false);
            if (e.getValue() != null) {
                sessionBuilding.setItems(buildingService.getBuildingsByUniversity(e.getValue().getName()));
                sessionBuilding.setEnabled(true);
            } else {
                sessionBuilding.setEnabled(false);
            }
        });

        sessionBuilding.addValueChangeListener(e -> {
            sessionRoom.clear();
            if (e.getValue() != null) {
                sessionRoom.setItems(roomService.getRoomByBuilding(e.getValue().getId()));
                sessionRoom.setEnabled(true);
            } else {
                sessionRoom.setEnabled(false);
            }
        });
        
        //Layout filters
        FormLayout filters = new FormLayout();
        onlyAvailable.getStyle().set("margin-top", "20px");
        filters.add(sessionSubject, fromDate, toDate, sessionUni, sessionBuilding, sessionRoom, onlyAvailable);
        filters.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                                   new FormLayout.ResponsiveStep("600px", 3));

        searchSessionBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchSessionBtn.addClickListener(e -> performSessionSearch());
        
        Button resetBtn = new Button("Reset", e -> {
            sessionSubject.clear();
            fromDate.clear();
            toDate.clear(); 
            sessionUni.clear();
            onlyAvailable.clear();
            sessionCardsContainer.removeAll();
            LocalDate now = LocalDate.now();
            fromDate.setMin(now);
            fromDate.setMax(null);
            fromDate.setValue(now);
            toDate.setMin(now);
            toDate.setMax(null);
        });
        
        HorizontalLayout actions = new HorizontalLayout(searchSessionBtn, resetBtn);
        
        sessionCardsContainer.setPadding(false);
        sessionCardsContainer.setSpacing(true);
        sessionCardsContainer.setWidthFull();
        sessionCardsContainer.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        
        Scroller scroller = new Scroller(sessionCardsContainer);
        scroller.setSizeFull();
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        
        SessionDetails = new Details(new H4("Filter Sessions"), filters, actions);
        
        SessionDetails.setOpened(true);
        SessionDetails.setWidthFull();
        
        H4 resultsTitle = new H4("Search Results");
        
        VerticalLayout layout = new VerticalLayout(SessionDetails, resultsTitle, scroller);
        layout.setSizeFull();
        layout.setPadding(false);
        
        return layout;
    }

    private void performSessionSearch() {
    	//Get criteria from filters
        SessionSearchCriteria criteria = new SessionSearchCriteria();
        criteria.setSubject(sessionSubject.getValue());
        criteria.setFromDate(fromDate.getValue());
        criteria.setToDate(toDate.getValue());
        criteria.setUniversity(sessionUni.getValue());
        criteria.setBuilding(sessionBuilding.getValue());
        criteria.setRoom(sessionRoom.getValue());
        criteria.setOnlyAvailable(onlyAvailable.getValue());

        //Get the list of results from the Service
        List<StudySession> results = sessionService.findSessions(criteria);

        //Clean old results
        sessionCardsContainer.removeAll();

        //Handling "Zero result"
        if (results.isEmpty()) {
            noResults.setText("No sessions found matching these criteria.");
            sessionCardsContainer.add(noResults);
        } else {
        	SessionDetails.setOpened(false);
            //SessionCard creation
            for (StudySession session : results) {
                SessionCard card = new SessionCard(session.getId(), securityService, userService, sessionService, sessionInvitationService);
                
                sessionCardsContainer.add(card);
            }
        }
    }

    // ==========================================
    // USER SECTION
    // ==========================================
    private void createUserPage() {
        //Filter configuration
        userDept.setEnabled(false);
        userCourse.setEnabled(false);
        minReputation.setMin(0);
        minReputation.setMax(5);
        minReputation.setStep(0.1);
        minReputation.setStepButtonsVisible(true);

        //Waterfall logic (University -> Building -> Room)
        userUni.addValueChangeListener(e -> {
            userDept.clear();
            userCourse.clear();
            userCourse.setEnabled(false);
            if (e.getValue() != null) {
                userDept.setItems(departmentService.getDepartmentsByUniversity(e.getValue()));
                userDept.setEnabled(true);
            } else {
                userDept.setEnabled(false);
            }
        });

        userDept.addValueChangeListener(e -> {
            userCourse.clear();
            if (e.getValue() != null) {
                userCourse.setItems(studyCourseService.getCoursesByDepartment(e.getValue()));
                userCourse.setEnabled(true);
            } else {
                userCourse.setEnabled(false);
            }
        });

        FormLayout filters = new FormLayout();
        filters.add(firstNameFilter, lastNameFilter, usernameFilter, userUni, userDept, userCourse, skillFilter, minReputation);
        filters.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                                   new FormLayout.ResponsiveStep("600px", 3));

        searchUserBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchUserBtn.addClickListener(e -> performUserSearch());
        
        Button resetUserBtn = new Button("Reset", e -> {
        	firstNameFilter.clear(); 
            lastNameFilter.clear(); 
            usernameFilter.clear();
            userUni.clear();
            skillFilter.clear();
            minReputation.clear();
            userGrid.setItems();
            userPage.remove(noResults);
        });

        HorizontalLayout actions = new HorizontalLayout(searchUserBtn, resetUserBtn);

        configureUserGrid();
        
        UserDetails = new Details(new H4("Filter User"), filters, actions);
        
        UserDetails.setOpened(true);
        UserDetails.setWidthFull();
        
        H4 resultsTitle = new H4("Search Result");

        userPage.add(UserDetails, resultsTitle, userGrid);
        userPage.setSizeFull();
        userPage.setPadding(false);
    }

    private void configureUserGrid() {
        //Avatar rendering
        userGrid.addComponentColumn(profile -> {
            Avatar avatar = new Avatar(profile.getUser().getUsername());
            avatar.addThemeName("xsmall");
            avatar.getStyle().set("background-color", "f3f1e9").set("color", "#0f172a");//0f172a
            if (profile.getProfilePicture() != null && profile.getProfilePicture().length > 0) {
                StreamResource resource = new StreamResource("avatar", 
                    () -> new ByteArrayInputStream(profile.getProfilePicture()));
                avatar.setImageResource(resource);
            }
            return avatar;
        }).setHeader("Avatar").setAutoWidth(true).setFlexGrow(0);

        userGrid.addColumn(p -> p.getFirstName() + " " + p.getLastName()).setHeader("Name").setSortable(true);
        userGrid.addColumn(p -> p.getUser().getUsername()).setHeader("Username");
        
        userGrid.addColumn(p -> p.getUniversity() != null ? p.getUniversity().getName() : "-").setHeader("University");
        
        // Renderer Reputazione (Stelle)
        userGrid.addComponentColumn(profile -> {
             double rep = profile.getReputation() != null ? profile.getReputation() : 0.0;
             return new Div(new com.vaadin.flow.component.html.Span(String.format("%.1f ★", rep)));
        }).setHeader("Reputation").setSortable(true);

        //Action column
        userGrid.addComponentColumn(profile -> {
            Button viewProfileBtn = new Button("See profile");
            viewProfileBtn.addClickListener(e -> {
                 //Navigate to PersonaArea throw username
                 getUI().ifPresent(ui -> ui.navigate(PersonalArea.class, profile.getUser().getUsername()));
            });
            
            Button followBtn = new Button("Follow");
            followBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            followBtn.addClickListener(e -> Notification.show("You start to follow " + profile.getUser().getUsername()));
            
            return new HorizontalLayout(viewProfileBtn, followBtn);
        }).setHeader("Actions");
        
        userGrid.setSizeFull();
    }

    private void performUserSearch() {
    	//Get criteria from filters
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setFirstName(firstNameFilter.getValue());
        criteria.setLastName(lastNameFilter.getValue());
        criteria.setUsername(usernameFilter.getValue());
        criteria.setUniversity(userUni.getValue());
        criteria.setCourse(userCourse.getValue());
        criteria.setSkill(skillFilter.getValue());
        criteria.setMinReputation(minReputation.getValue());
        
        //Get the list of results from the Service
        List<UserProfile> results = profileService.findProfiles(criteria);
        
        //Handling "Zero result"
        if (results.isEmpty()) {
            userGrid.setVisible(false);
            noResults.setText("No users found matching these criteria.");
            userPage.add(noResults);
            userPage.setHorizontalComponentAlignment(Alignment.CENTER, noResults);
        } else {
        	UserDetails.setOpened(false);
        	//Show the results
        	userGrid.setVisible(true);
        	userPage.remove(noResults);
        	userGrid.setItems(profileService.findProfiles(criteria));
        }
    }
    
    private void loadInitialData() {
        sessionSubject.setItems(subjectService.getAllSubjects());
        skillFilter.setItems(subjectService.getAllSubjects());
        
        sessionUni.setItems(universityService.getAllUniversities());
        userUni.setItems(universityService.getAllUniversities());
    }
}
