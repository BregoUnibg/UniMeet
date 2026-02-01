package local.unimeet.ui.sessionview;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.avatar.AvatarGroup;
import com.vaadin.flow.component.avatar.AvatarGroup.AvatarGroupItem;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import local.unimeet.entity.CourseSubject;
import local.unimeet.entity.SessionType;
import local.unimeet.entity.StudySession;
import local.unimeet.entity.User;
import local.unimeet.security.SecurityService;
import local.unimeet.service.StudySessionService;
import local.unimeet.service.UserService;

public class SessionCard extends Div{
	
	private final SecurityService securityService;
	private final UserService userService;
	private final StudySessionService studySessionService;
	private StudySession studySession;
	
	public SessionCard(SecurityService securityService, UserService userService, StudySessionService studySessionService) {
		
		this.studySessionService = studySessionService;
		this.securityService = securityService;
		this.userService = userService;
		
		//Keeping this as a sample
		//MUST BE DELETED
		
		this.setMaxWidth("800px");
		this.getStyle().set("background-color", "white");
		
		VerticalLayout card = new VerticalLayout();
		HorizontalLayout top = new HorizontalLayout();
		HorizontalLayout bottom = new HorizontalLayout();
		
		card.setSpacing(false);
		
		top.setWidthFull();
		top.setMaxHeight("150px");
		bottom.setWidthFull();
		bottom.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
		
		
		//Location
		VerticalLayout left = new VerticalLayout();
		Span university = new Span ("University");
		Span buildingAndRoom = new Span ("A" + "001");
		Span address = new Span ("St. Patrick's s. 23");
		
		left.add(university, buildingAndRoom, address);
		
		
		//subject aveilability & description
		VerticalLayout center = new VerticalLayout();
		
		HorizontalLayout badges = new HorizontalLayout();
		
		Span type = new Span("Public");
		type.getElement().getThemeList().add("badge pill");
		
		Span subject = new Span("Calculus I");
		subject.getElement().getThemeList().add("badge pill");
		subject.getStyle().setBackgroundColor("red");
		
		badges.add(type, subject);
		
		Span description = new Span("Whis session is ment for those who have a good undertanding of limits and are just starting out integrating. if you are intrested please once you show up respect our code of conduct whichi includes the following princeples: rule n 1 never argua allways confront");
		
		description.addClassNames(
	            LumoUtility.Overflow.HIDDEN        //Hide text that does not fit
	            
	    );
		
		center.add(badges, description);
		
		
		//Time 
		VerticalLayout right = new VerticalLayout();
		
		Span date = new Span("12 March");
		Span time = new Span("10:30" + " - " + "12:30");
		
		Span availableSeats = new Span("1/6");
		availableSeats.getElement().getThemeList().add("badge success");
		
		right.add(date, time, availableSeats);
		
		

		
		//Make it so left and right pane take up 1/4 of the total lenght each
		left.setWidth("25%");
		center.setWidth("50%");
		right.setWidth("25%");
		top.add(left, center, right);
		
		
		//Student avatar Icons and join button
		AvatarGroup avatarParticipants = new AvatarGroup();
		
	    AvatarGroupItem avatar = new AvatarGroupItem("Jhon Deer");
	    avatar.setColorIndex(1);
	    avatarParticipants.add(avatar);
	    
	    
		/*
		 * Vaadin docs sample avatar group 
		 * 
		 * 	AvatarGroup avatarGroup = new AvatarGroup();

			for (Person person : people) {
			    String name = person.getFirstName() + " " + person.getLastName();
			    AvatarGroupItem avatar = new AvatarGroupItem(name);
			    avatar.setColorIndex(colorIndex++);
			    avatarGroup.add(avatar);
			}
		 * 
		 * */
		
		
	    //Dual Button logic - once I click join I can click leave and vice versa
	    
	    Button joinButton = new Button("join");
	    joinButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
	    joinButton.setWidth("150px");
	    
	    
	    avatarParticipants.getStyle().set("margin-left", "16px");
	    joinButton.getStyle().set("margin-left", "16px");
	    
	    VerticalLayout bottomMaskLeft = new VerticalLayout();
	    VerticalLayout bottomMaskCenter = new VerticalLayout();
	    VerticalLayout bottomMaskRight = new VerticalLayout();
	    
	    bottomMaskLeft.setWidth("25%");
	    bottomMaskCenter.setWidth("50%");
	    bottomMaskRight.setWidth("25%");
	    
	    bottomMaskLeft.setPadding(false);
	    bottomMaskCenter.setPadding(false);
	    bottomMaskRight.setPadding(false);
	    
	    bottomMaskLeft.add(avatarParticipants);
	    bottomMaskRight.add(joinButton);
	    
	    bottom.add(bottomMaskLeft, bottomMaskCenter, bottomMaskRight);
	    
	    
	    card.add(top, bottom);
	    
	    this.add(card);
	    
	    
	    this.addClassNames(
	    	    //external border shadow
	    	    LumoUtility.BoxShadow.SMALL, 	    	    
	    	    //Internal padiing
	    	    LumoUtility.Padding.XSMALL,
	    	    //External margin
	    	    LumoUtility.Margin.MEDIUM    
	  		);
	    
	    //rounded smooth corners
	    this.getStyle().set("border-radius", "20px");
	    
	}
	
	
	public SessionCard(Long studySessionId, SecurityService securityService, UserService userService, StudySessionService studySessionService) {
		
		this.securityService = securityService;
		this.userService = userService;
		this.studySessionService = studySessionService;
		
		this.studySession = this.studySessionService.getStudySessionById(studySessionId);

		//Keeping this as a sample
		
		this.setWidth("100%");
		this.setMaxWidth("1000px");
		this.getStyle().set("background-color", "white");
		
		VerticalLayout card = new VerticalLayout();
		HorizontalLayout top = new HorizontalLayout();
		HorizontalLayout bottom = new HorizontalLayout();
		
		card.setSpacing(false);
		
		top.setWidthFull();
		top.setMaxHeight("150px");
		bottom.setWidthFull();
		bottom.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
		
		
		//Location
		VerticalLayout left = new VerticalLayout();
		//left.setSpacing(false);
		Span university = new Span (studySession.getUniversity().getName());
		//university.getStyle().set("margin-bottom", "5px");
		Span buildingAndRoom = new Span (studySession.getBuilding().getName() + studySession.getRoom().getNumber());
		//buildingAndRoom.getStyle().set("margin-bottom", "5px");
		Span address = new Span (studySession.getAddress());
		
		left.add(university, buildingAndRoom, address);
		
		
		//subject aveilability & description
		VerticalLayout center = new VerticalLayout();
		
		HorizontalLayout badges = new HorizontalLayout();
		
		Span type = new Span(getFormattedSessionType(studySession.getType()));
		type.getElement().getThemeList().add("badge pill");
		
		Span subject = new Span(getFormattedSubjectName(studySession.getSubject()));
		subject.getElement().getThemeList().add("badge pill");
		subject.getStyle().set("color", "#8B0836");
		subject.getStyle().setBackgroundColor("#FFF0F1");
		
		badges.add(type, subject);
		
		Span description = new Span(studySession.getDescription());
		
		description.addClassNames(
	            LumoUtility.Overflow.HIDDEN        //Hide text that does not fit
	            
	    );
		
		center.add(badges, description);
		
		
		//Time 
		VerticalLayout right = new VerticalLayout();
		
		Span date = new Span(this.getFormattedString(studySession.getDate().getMonth().toString() + " " + studySession.getDate().getDayOfMonth()));
		Span time = new Span(studySession.getTimeStart() + " - " + studySession.getTimeEnd());
		
		Span availableSeats = new Span(studySession.getCountMembers() + "/6");
		
		availableSeats.getElement().getThemeList().add(costumBadgeColor(studySession.getCountMembers(), 6));
		
		right.add(date, time, availableSeats);
		
		

		
		//Make it so left and right pane take up 1/4 of the total lenght each
		left.setWidth("25%");
		center.setWidth("50%");
		right.setWidth("25%");
		top.add(left, center, right);
		
		
		//Student avatar Icons and join button
		AvatarGroup avatarParticipants = new AvatarGroup();
		
	    AvatarGroupItem avatar = new AvatarGroupItem(studySession.getOwner().getUsername());
	    avatar.setColorIndex(1);
	    avatarParticipants.add(avatar);
	    
	    int i = 2;
	    
	    AvatarGroupItem myAvatar = null;
	    
	    for (User partecipant: studySession.getParticipants()) {
		    String name = partecipant.getUsername();
		    AvatarGroupItem a = new AvatarGroupItem(name);
		    a.setColorIndex(i++);
		    avatarParticipants.add(a);
		    
		    if(this.securityService.getAuthenticatedUsername().equals(partecipant.getUsername())){
		    	myAvatar = a;
		    }
		    
		}
	    
	    
	    Div buttonWrapper = new Div();
	    buttonWrapper.setWidth("200px");
	    
	    Button joinButton = new Button("join");
	    joinButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
	    joinButton.setWidth("100%");
	    joinButton.getStyle().set("margin-inline", "0px");
	    
	    Button leaveButton = new Button("leave");
	    leaveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
	    leaveButton.setWidth("100%");
	    
	    buttonWrapper.add(joinButton, leaveButton);
	    
	    
	    //Both bust exists but I just render the right one, opnly way to make it owrk in vaadin or html in general it seems
	    joinButton.setVisible(true);
	    leaveButton.setVisible(false);
	    
	    //Join button logic
	    
	    String currentUsername =  this.securityService.getAuthenticatedUsername();
	    ArrayList<User> studySessionParticipants =  (ArrayList<User>) studySession.getParticipantsAndOwner();
	    
	    boolean isAlreadyInSession = false;
	    
	    //Set button as unavailable is i'm the owner
	    if(studySession.getOwner().getUsername().equals(currentUsername)) {
	    		joinButton.setEnabled(false);
	    		joinButton.setText("Owned");
	    		joinButton.getStyle().setBackground("Gray");
	    		leaveButton.setEnabled(false);
	    		leaveButton.setText("Owned");
	    		leaveButton.getStyle().setBackground("Gray");
	    		
	    }

	    //Can't use equals or contains because of hybernate lazy exception
	    for(User u: studySessionParticipants) {
	    	if(u.getUsername().equals(currentUsername)) {
	    		isAlreadyInSession = true;
	    	}
	    }
	    
	    if(isAlreadyInSession){
	    	joinButton.setVisible(false);
		    leaveButton.setVisible(true);
	    }
	    
	    //Needs to be final not to break everything for some reason ??
	    final int ii = i + 1;
	    
	    
	    if(myAvatar == null) {
	    	myAvatar = new AvatarGroupItem(currentUsername);
	    	myAvatar.setColorIndex(ii); 
	    }
	    
	    
	    final AvatarGroupItem myAvatarWrapper = myAvatar;
	    
	    joinButton.addClickListener(event -> {
	        try {
	            studySessionService.addPartecipant(studySession, currentUsername);
	            
	            joinButton.setVisible(false);
			    leaveButton.setVisible(true);
	            Notification.show("Joined successfully!");
	            

	            avatarParticipants.add(myAvatarWrapper);
	            
	            availableSeats.setText(studySession.getCountMembers() + "/6");
	            availableSeats.getElement().getThemeList().clear();
	            availableSeats.getElement().getThemeList().add(costumBadgeColor(studySession.getCountMembers(), 6));
	    		
	            
	        } catch (Exception e) {
	            Notification.show("Error joining session: " + e.getMessage());
	        }
	    });
	    
	    
	    leaveButton.addClickListener(event -> {
	        try {
	            studySessionService.removePartecipant(studySession, currentUsername);
	            
	            joinButton.setVisible(true);
			    leaveButton.setVisible(false);
	            Notification.show("Left successfully!");
	            
	            avatarParticipants.remove(myAvatarWrapper);
	            
	            availableSeats.setText(studySession.getCountMembers() + "/6");
	            availableSeats.getElement().getThemeList().clear();
	            availableSeats.getElement().getThemeList().add(costumBadgeColor(studySession.getCountMembers(), 6));
	    		
	            
	        } catch (Exception e) {
	            Notification.show("Error joining session: " + e.getMessage());
	        }
	    });
	    
	    
	    avatarParticipants.getStyle().set("margin-left", "16px");
	    joinButton.getStyle().set("margin-left", "16px");
	    
	    VerticalLayout bottomMaskLeft = new VerticalLayout();
	    VerticalLayout bottomMaskCenter = new VerticalLayout();
	    VerticalLayout bottomMaskRight = new VerticalLayout();
	    
	    bottomMaskLeft.setWidth("25%");
	    bottomMaskCenter.setWidth("50%");
	    bottomMaskRight.setWidth("25%");
	    
	    bottomMaskLeft.setPadding(false);
	    bottomMaskCenter.setPadding(false);
	    bottomMaskRight.setPadding(false);
	    
	    bottomMaskLeft.add(avatarParticipants);
	    bottomMaskRight.add(buttonWrapper);
	    
	    bottom.add(bottomMaskLeft, bottomMaskCenter, bottomMaskRight);
	    
	    
	    card.add(top, bottom);
	    
	    this.add(card);
	    
	    
	    this.addClassNames(
	    	    //external border shadow
	    	    LumoUtility.BoxShadow.SMALL, 	    	    
	    	    //Internal padiing
	    	    LumoUtility.Padding.XSMALL,
	    	    //External margin
	    	    LumoUtility.Margin.MEDIUM    
	  		);
	    
	    //rounded smooth corners
	    this.getStyle().set("border-radius", "20px");
	    
	}
	
	/**
	 * Returns a decently formattted string instead of the default enum.toString () output
	 * @param subject
	 * @return
	 */
	
	private String getFormattedSubjectName(CourseSubject subject) {
		
		if(subject.equals(CourseSubject.CALCULUS_II))
			return "Calculus II";
			
		return getFormattedString(subject.name());
		
	}
	
	/**
	 * Return a decently formatted string instead of the default unum.toString() output
	 * @param ss
	 * @return
	 */
	
	private String getFormattedSessionType(SessionType ss) {
		
		return getFormattedString(ss.name());
		
	}
	
	/**
	 * Returns formatted string
	 * @param string
	 * @return
	 */
	
	private String getFormattedString(String string) {
		
		String [] words = string.split("_");
		
		String output = "";
		
		for(String word: words) {
			output += word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase() + " ";
		}
		
		output.substring(0, output.length()-1);
		
		return output;
		
		
	}
	
	/**
	 * Return badge parameter to set color based how many seats are left 
	 * @param members
	 * @param max
	 * @return
	 */
	private String costumBadgeColor(int members, int max) {
		
		if(members==max)
			return "badge error";
		
		if((double)members/max >= 0.5)
			return "badge warning";
		
		return "badge success";
	}

}
