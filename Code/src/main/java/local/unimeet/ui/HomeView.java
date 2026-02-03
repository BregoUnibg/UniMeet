package local.unimeet.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.PermitAll;
import local.unimeet.entity.StudySession;
import local.unimeet.entity.Subject;
import local.unimeet.entity.User;
import local.unimeet.entity.UserProfile;
import local.unimeet.repository.UserRepository;
import local.unimeet.security.SecurityService;
import local.unimeet.service.ColleagueRequestService;
import local.unimeet.service.ProfileService;
import local.unimeet.service.SessionInvitationService;
import local.unimeet.service.StudySessionService;
import local.unimeet.service.UserService;
import local.unimeet.ui.sessionview.SessionCard;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Home")
@PermitAll
public class HomeView extends VerticalLayout {


	private final StudySessionService studySessionService;
	private final SecurityService securityService;
	private final SessionInvitationService sessionInvitationService;
	private final UserService userService;
	private final ProfileService profileService;
	private final ColleagueRequestService colleagueRequestService; 
	private final UserRepository userRepository;
	
	
    public HomeView(StudySessionService studySessionService, SecurityService securityService, UserService userService, SessionInvitationService sessionInvitationService,
    				ProfileService profileService, UserRepository userRepository, ColleagueRequestService colleagueReqeustService) {
        
    	this.studySessionService =	studySessionService;
    	this.securityService = securityService;
    	this.sessionInvitationService = sessionInvitationService;
    	this.userService = userService;
    	this.profileService = profileService;
    	this.userRepository = userRepository;
    	this.colleagueRequestService = colleagueReqeustService;
    	
    	
        addClassName("dashboard-view");
        setPadding(true); 
        setSpacing(true);

        // Banner Premium
        add(createPremiumBanner());

        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setPadding(false); 
        mainContent.setSpacing(true);
        mainContent.setWidthFull();

        // Stats
        //mainContent.add(createStatsRow());

        // Tabs
        
        TabSheet tabs = new TabSheet();
        
        
        //TEMP
        /*
        StudySession temp = new StudySession();
        
        temp.setUniversity("Universita di Bergamo");
        temp.setBuilding("A");
        temp.setRoom("001");
        temp.setAddress("Via dei cavalli");
        temp.setType(SessionType.PUBLIC);
        temp.setSubject(CourseSubject.CALCULUS_I);
        temp.setDescription("very long and persuasive descipraelfdnasnmdasnkdbaskjdasjd asd ashd ashd ashd ashjd ashd ashd asj dasjhd asj ");
        temp.setDate(LocalDate.of(2025, 8, 12));
        temp.setTimeStart(LocalTime.of(9, 30));
        temp.setTimeEnd(LocalTime.of(10, 30));
        temp.setOwner(userService.getUserByUsername("brego"));
        temp.addPartecipant(userService.getUserByUsername("paolo"));
        temp.addPartecipant(userService.getUserByUsername("diego"));
        
        this.studySessionService.saveStudySession(temp);
        */
        //TEMP
        

        VerticalLayout joinedSessionContent = new VerticalLayout();
        VerticalLayout colleaguesSessionContent = new VerticalLayout();
        VerticalLayout suggestedSessionContent = new VerticalLayout();
        
        joinedSessionContent.setWidth("1200px");
        joinedSessionContent.setHeightFull();
        joinedSessionContent.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        
        colleaguesSessionContent.setWidth("1200px");
        colleaguesSessionContent.setHeightFull();
        colleaguesSessionContent.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        
        
        suggestedSessionContent.setWidth("1200px");
        suggestedSessionContent.setHeightFull();
        suggestedSessionContent.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        
        
        
        tabs.add("Joined Sessions", joinedSessionContent);
        tabs.add("Colleagues' Sessions", colleaguesSessionContent);
        tabs.add("Suggested Sessions", suggestedSessionContent);
        
        tabs.addClassNames(LumoUtility.Display.FLEX,
        	    LumoUtility.JustifyContent.CENTER,
        	    LumoUtility.AlignItems.CENTER
        );
        
        List<StudySession> currentUserJoinedSessions = this.studySessionService.getStudySessionsByParticipant(this.securityService.getAuthenticatedUsername());
        
        for(StudySession ss: currentUserJoinedSessions) {
        	
        	joinedSessionContent.add(new SessionCard(ss.getId(), securityService, userService, studySessionService, sessionInvitationService));         	
        	
        }
        
        
        List<StudySession> colleagueSessions = this.studySessionService.getSessionsFromColleagues(this.securityService.getAuthenticatedUsername());
        
        for(StudySession ss: colleagueSessions) {
        	
        	colleaguesSessionContent.add(new SessionCard(ss.getId(), securityService, userService, studySessionService, sessionInvitationService));         	
        	
        }
        
        /*List<StudySession> currentUserFriendsSessions = currentUserFriendsSessions();
        
        for(StudySession ss: currentUserFriendsSessions) {
        	
        	friendSessionContent.add(new SessionCard(ss.getId(), securityService, userService, studySessionService, sessionInvitationService));         	
        	
        }*/
         

        List<StudySession> currentUserSaggestedSessions = currentUserSaggestedSessions();
        
        for(StudySession ss: currentUserSaggestedSessions) {
        	
        	suggestedSessionContent.add(new SessionCard(ss.getId(), securityService, userService, studySessionService, sessionInvitationService));         	
        	
        }
        
        
        tabs.setWidthFull();
        tabs.setHeightFull();
        tabs.addClassName(LumoUtility.Margin.Top.MEDIUM);

        
        
        mainContent.add(tabs);
        add(mainContent);
    }
    
    private UserProfile getCurrentProfile() {
    	String username = SecurityContextHolder.getContext().getAuthentication().getName();
    	User currentUser = userRepository.findById(username)
    	    .orElseThrow(() -> new RuntimeException("Utente non trovato"));
    	return profileService.getOrCreateProfile(currentUser);
    }

    
    /**
     * Returns every studysession owned by the friends of the authenticather user
     * @return
     */
    
    /*private List<StudySession> currentUserFriendsSessions(){
    	/*StudySessionService studySer = studySessionService;
    	SecurityService secSer = securityService;
    	
    	//Prendo lista di tutti gli utenti
    	Set<UserProfile> friends = getCurrentProfile().getFriends();
    	
    	List<StudySession> sessions = new ArrayList<StudySession>();
    	
    	for(UserProfile owner : friends) {
    		sessions.addAll(studySer.getStudySessionByOwner(owner.getUser().getUsername()));
    	}
    	
    	return sessions;
    }*/
    
    private List<StudySession> currentUserSaggestedSessions(){
    	StudySessionService studySer = studySessionService;
    	SecurityService secSer = securityService;
    	
    	//Prendo lista di tutti gli utenti
    	Set<Subject> pendingExams = getCurrentProfile().getPendingExams();
    	
    	List<StudySession> sessions = new ArrayList<StudySession>();
    	
    	for(Subject s : pendingExams) {
    		sessions.addAll(studySer.getStudySessionBySubject(s));
    	}
    	
    	return sessions;
    }
    
    
//here it's created the banner in the homeview
    private Component createPremiumBanner() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(username == null || username.isEmpty()) username = "Studente";
        else username = username.substring(0, 1).toUpperCase() + username.substring(1);

        HorizontalLayout banner = new HorizontalLayout();
        banner.setWidthFull(); banner.setMinHeight("180px"); 
        banner.addClassNames(LumoUtility.Padding.XLARGE, LumoUtility.BorderRadius.LARGE, LumoUtility.AlignItems.CENTER);
        banner.getStyle().set("background", "linear-gradient(135deg, #0f172a 0%, #1e293b 100%)");
        banner.getStyle().set("color", "white");
        banner.getStyle().set("box-shadow", "inset 0 1px 1px rgba(255,255,255,0.2), 0 10px 25px -5px rgba(0,0,0,0.3)");
        banner.getStyle().set("position", "relative"); banner.getStyle().set("overflow", "hidden");

        H1 title = new H1("Good to see you, " + username + "!");
        title.addClassNames(LumoUtility.Margin.NONE, LumoUtility.FontWeight.BOLD);
        title.getStyle().set("font-size", "2.5rem").set("color", "white");
        
        Span subtitle = new Span("Welcome back on UniMeet.");
        subtitle.addClassNames(LumoUtility.FontSize.LARGE);
        subtitle.getStyle().set("color", "white").set("font-weight", "500");

        VerticalLayout textContainer = new VerticalLayout(title, subtitle);
        textContainer.setPadding(false); textContainer.setSpacing(false);

        Icon decorationIcon = VaadinIcon.DIPLOMA_SCROLL.create();
        decorationIcon.setSize("200px"); decorationIcon.setColor("white");
        decorationIcon.getStyle().set("opacity", "0.1").set("position", "absolute").set("right", "-20px").set("bottom", "-30px").set("transform", "rotate(-15deg)");

        banner.add(textContainer, decorationIcon); banner.expand(textContainer);
        return banner;
    }
    
    private VerticalLayout createCard(String l, String v, Component i, String c) {
        VerticalLayout card = new VerticalLayout();
        card.addClassNames(LumoUtility.Background.BASE, LumoUtility.BoxShadow.XSMALL, LumoUtility.BorderRadius.MEDIUM, LumoUtility.Padding.MEDIUM);
        card.setMinWidth("220px"); card.setSpacing(false);
        if(c.equals("primary")) i.getStyle().set("color", "var(--lumo-primary-color)");
        if(c.equals("contrast")) i.getStyle().set("color", "#0f172a");
        card.add(i, new H3(v), new Span(l));
        return card;
    }
}