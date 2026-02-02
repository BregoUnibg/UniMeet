package local.unimeet.ui;

import java.io.ByteArrayInputStream;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import jakarta.annotation.security.PermitAll;
import local.unimeet.entity.Subject;
import local.unimeet.entity.User;
import local.unimeet.entity.UserProfile;
import local.unimeet.repository.UserRepository;
import local.unimeet.security.SecurityService;
import local.unimeet.service.ColleagueRequestService;
import local.unimeet.service.ProfileService;
import local.unimeet.service.UserService;

@Route(value = "personal-area", layout = MainLayout.class)
@PageTitle("Personal Profile")
@PermitAll
public class PersonalArea extends VerticalLayout implements HasUrlParameter<String> {
	
	private UserProfile currentProfile;
    private final ProfileService profileService;
    private final UserService userService;
	private final DelegationInterface delegate;
	private final ColleagueRequestService colleagueRequestService;
	private final SecurityService securityService;

    public PersonalArea(ProfileService profileService, UserService userService, DelegationInterface delegate, ColleagueRequestService colleagueRequestService, SecurityService securityService) {
    	this.profileService = profileService;
        this.userService= userService;
    	this.delegate = delegate;
    	this.colleagueRequestService = colleagueRequestService;
    	this.securityService = securityService;
    }
    
    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String username) {
        removeAll();

        String targetUsername;
        if (username == null || username.isEmpty()) {
        	//If URL is just /personal-area, show logged-in user
            targetUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        } else {
        	//If URL is /personal-area/something, show that specific profile
            targetUsername = username;
        }

        //Load profile from database
        Optional.ofNullable(userService.getUserByUsername(targetUsername))
        .ifPresentOrElse(user -> {
            this.currentProfile = profileService.getOrCreateProfile(user);
            buildLayout();
        }, () -> {
            add(new H2("User not found"));
        });
    }
    
    private void buildLayout() {
    	boolean isOwner = SecurityContextHolder.getContext().getAuthentication().getName().equals(currentProfile.getUser().getUsername());
    	
    	//General page settings
        setSpacing(true);
        setPadding(true);
        setMaxWidth("800px");
        getElement().getStyle().set("margin", "auto");
        
        H2 pageTitol = new H2("Personal area");
        pageTitol.getStyle().set("margin", "0");
        
        HorizontalLayout titleFrindButton = new HorizontalLayout();
        titleFrindButton.setWidthFull();
        titleFrindButton.setAlignItems(Alignment.CENTER);
        titleFrindButton.setJustifyContentMode(JustifyContentMode.BETWEEN);
        
        
        
        
        //Friendship buttons
        Div buttonWrapper = new Div();
	    
	    Button addColleagueButton = new Button("Add");
	    addColleagueButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
	    addColleagueButton.setVisible(!isOwner);
	    
	    Button removeColleagueButton = new Button("Remove");
	    removeColleagueButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
	    removeColleagueButton.setVisible(!isOwner);
	    
	    buttonWrapper.add(addColleagueButton, removeColleagueButton);
	    
	    //Both must exists but I just render the right one, only way to make it work in vaadin or html in general it seems
	    addColleagueButton.setVisible(!isOwner);
	    removeColleagueButton.setVisible(false);
	    
	    addColleagueButton.setEnabled(!isOwner);
	    removeColleagueButton.setEnabled(!isOwner);
	    
	    User currentUser = this.userService.getUserByUsername(this.securityService.getAuthenticatedUsername());
	    User profileUser = this.currentProfile.getUser();
	    
	    
	    //If request is pengind see addColleague but can't interract with it
	    if(this.colleagueRequestService.isRequestPending(currentUser, profileUser)) {
	    	addColleagueButton.setEnabled(false);
	    }
	    
	    
	    //If they area colleagues enable removeColleague Button
	    if(this.colleagueRequestService.areColleagues(currentUser, profileUser)){
	    	addColleagueButton.setVisible(false);
		    removeColleagueButton.setVisible(true);
	    }
	    
	    
	    	
	    addColleagueButton.addClickListener(event -> {
	        try {
	            
	        	this.colleagueRequestService.sendRequest(currentUser, profileUser);
	            Notification.show("Request Succesfully sent to: " + profileUser.getUsername());
	            
	            addColleagueButton.setEnabled(false);
	            
	            
	        } catch (Exception e) {
	            Notification.show("Error sending reqeust: " + e.getMessage());
	        }
	    });
	    
	    
	    removeColleagueButton.addClickListener(event -> {
	        try {
	            
	        	this.colleagueRequestService.removeColleague(currentUser, profileUser);
	        	Notification.show("You are no longer colleagues");
	            
	            addColleagueButton.setVisible(true);
	            removeColleagueButton.setVisible(false);
	            removeColleagueButton.setEnabled(false);
	            
	            
	        } catch (Exception e) {
	            Notification.show("Error: " + e.getMessage());
	        }
	        
	    });
	    
	    //IMPLEMENT SECURITYSERVICE AND MANAGE FIREND REQUESTS WHEN CLICKING ADD ADD REMOVE FRIENDS BUTTON
	    //IF REQEUST IS PENDING BUTTONS ARE DISABLED
	    
	    
	    titleFrindButton.add(pageTitol, buttonWrapper);
	    add(titleFrindButton);

	    
	    
        //--- PERSONAL DETAILS SECTION ---
        add(createAnagrafeSection(currentProfile));

        add(new Hr());

        //--- STUDY PATH SECTION ---
        add(new H3("STUDY PATH"));
        add(createStudyPathSection(currentProfile));

        add(new Hr());

        //--- CAREER SECTION (Courses and Exams) ---
        add(new H3("CAREER"));
        add(createCareerSection(currentProfile));

        //--- EDIT BUTTON ---
        Button editButton = new Button("Edit Profile", e -> getUI().ifPresent(ui -> ui.navigate(EditProfile.class)));
        editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        editButton.getStyle().set("margin-top", "2em");
        editButton.setVisible(isOwner);
        
        HorizontalLayout footer = new HorizontalLayout(editButton);
        footer.setWidthFull();
        footer.setJustifyContentMode(JustifyContentMode.END);
        add(footer);
    }

	private HorizontalLayout createAnagrafeSection(UserProfile profile) {
        //Profile picture (Placeholder)
        Image profileImg;

		if (profile.getProfilePicture() != null) {
			//If there is a photo in the DB, create resource from bytes
		    StreamResource resource = new StreamResource("profile-pic",
		            () -> new ByteArrayInputStream(profile.getProfilePicture()));
		    profileImg = new Image(resource, "Foto profilo");
		} else {
			//Otherwise use the placeholder
		    profileImg = new Image("images/default-avatar.jpeg", "Foto profilo predefinita");
		}

		profileImg.setWidth("100px");
        profileImg.setHeight("120px");
        profileImg.getStyle().set("border-radius", "8px");
        profileImg.getStyle().set("object-fit", "cover");
		profileImg.getStyle().set("border", "2px solid #ccc");
		

        VerticalLayout info = new VerticalLayout();
        info.setSpacing(false);
        info.setPadding(false);

        //Personal Data Fields
        info.add(delegate.createDataRow("First Name:", (profile.getFirstName() != null && !profile.getFirstName().isEmpty()) ? profile.getFirstName() : "Not specified"));
        info.add(delegate.createDataRow("Last Name:", (profile.getLastName() != null && !profile.getLastName().isEmpty()) ? profile.getLastName() : "Not specified"));
        
        Span labelBio = new Span("Bio: ");
        labelBio.setWidth("150px");
        labelBio.getStyle().set("font-weight", "bold");
        Span bio = new Span((profile.getBio() != null && !profile.getBio().isEmpty()) ? profile.getBio() : "No biography inserted.");
        bio.setWidth("394px");
        info.add(new HorizontalLayout(labelBio, bio));

        //Reputation
        double reputetion = profile.getReputation() != null ? profile.getReputation().doubleValue() : 0;
        int totVoters = profile.getTotVoters() != null ? profile.getTotVoters().intValue() : 0;
        info.add(delegate.createDataRow("Reputation: ", delegate.createReputationBar(reputetion, totVoters)));

        HorizontalLayout anagrafeLayout = new HorizontalLayout(profileImg, info);
        anagrafeLayout.setAlignItems(Alignment.START);
        return anagrafeLayout;
    }

    private VerticalLayout createStudyPathSection(UserProfile profile) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(false);
        layout.setPadding(false);
        
        layout.add(delegate.createDataRow("University:", profile.getUniversity() != null ? profile.getUniversity().getName() : "Not specified"));
        layout.add(delegate.createDataRow("Department:", profile.getDepartment() != null ? profile.getDepartment().getName() : "Not specified"));
        layout.add(delegate.createDataRow("Degree Type:", profile.getDegreeType() != null ? delegate.stringNormalization(profile.getDegreeType().toString()) : "Not specified"));
        layout.add(delegate.createDataRow("Degree Name:", profile.getCourseName() != null ? profile.getCourseName().getName() : "Not specified"));
        layout.add(delegate.createDataRow("Year of Study:", profile.getStudyYear() != null ? profile.getStudyYear().toString() : "Not specified"));

        return layout;
    }

    private VerticalLayout createCareerSection(UserProfile profile) {
    	//Columns for Preferred and Difficult Courses
        VerticalLayout pref;
        if (profile.getPreferredCourses() != null && !profile.getPreferredCourses().isEmpty()) {
            pref = createLabeledList("Preferred courses:", profile.getPreferredCourses());
        } else {
        	pref = createLabeledList("Preferred courses:", "No course selected");
        }
        VerticalLayout diff;
        if (profile.getDifficultCourses() != null && !profile.getDifficultCourses().isEmpty()) {
            diff = createLabeledList("Difficult courses:", profile.getDifficultCourses());
        } else {
        	diff = createLabeledList("Difficult courses:", "No course selected");
        }

        //Columns for Passed and Pending Exams
        VerticalLayout passed;
        if (profile.getPassedExams() != null && !profile.getPassedExams().isEmpty()) {
        	passed = createLabeledList("Passed exams:", profile.getPassedExams());
        } else {
        	passed = createLabeledList("Passed exams", "No exam selected");
        }
        VerticalLayout pending;
        if (profile.getPendingExams() != null && !profile.getPendingExams().isEmpty()) {
            pending = createLabeledList("Pending exams:", profile.getPendingExams());
        } else {
        	pending = createLabeledList("Pending exams:", "No exam selected");
        }

        HorizontalLayout courses = new HorizontalLayout(pref, diff);
        courses.setWidthFull();
        
        HorizontalLayout exams = new HorizontalLayout(passed, pending);
        exams.setWidthFull();
        
        VerticalLayout layout = new VerticalLayout(courses, exams);
        layout.setPadding(false);
        
        return layout;
    }

    //Helper methods
    private VerticalLayout createLabeledList(String title, Set<Subject> list) {
        VerticalLayout container = new VerticalLayout(spanBold(title));
        
        list.forEach(subject -> 
        	container.add(new Span("• " + subject.getName())));
        
        container.setSpacing(false);
        container.setPadding(false);
        return container;
    }
    
    private VerticalLayout createLabeledList(String title, String s) {
    	VerticalLayout container = new VerticalLayout(spanBold(title), new Span(s));
        container.setSpacing(false);
        container.setPadding(false);
        return container;
    }
    
    private Span spanBold(String label) {
    	Span l = new Span(label);
        l.getStyle().set("font-weight", "bold");
        return l;
    }
}
