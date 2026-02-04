package local.unimeet.ui.sessionview;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarGroup;
import com.vaadin.flow.component.avatar.AvatarGroup.AvatarGroupItem;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;
import local.unimeet.entity.SessionType;
import local.unimeet.entity.StudySession;
import local.unimeet.entity.StudySessionStatus;
import local.unimeet.entity.User;
import local.unimeet.exception.StudentBusyElsewhereException;
import local.unimeet.security.SecurityService;
import local.unimeet.service.ReviewService;
import local.unimeet.service.SessionInvitationService;
import local.unimeet.service.StudySessionService;
import local.unimeet.service.UserService;

public class SessionCard extends Div{
	
	private final SecurityService securityService;
	private final UserService userService;
	private final StudySessionService studySessionService;
	private final SessionInvitationService sessionInvitationService;
	private final ReviewService reviewService;
	private StudySession studySession;
	
	public SessionCard(Long studySessionId, SecurityService securityService, UserService userService, StudySessionService studySessionService, SessionInvitationService sessionInvitationService, ReviewService reviewService) {
		
		this.securityService = securityService;
		this.userService = userService;
		this.studySessionService = studySessionService;
		this.sessionInvitationService = sessionInvitationService;
		this.reviewService = reviewService;
		
		this.studySession = this.studySessionService.getStudySessionById(studySessionId);

		//Keeping this as a sample
		
		this.setWidth("100%");
		this.setMaxWidth("1000px");
		this.getStyle().set("background-color", "white");
		
		this.getStyle().set("position", "relative");
		
		VerticalLayout card = new VerticalLayout();
		HorizontalLayout top = new HorizontalLayout();
		HorizontalLayout bottom = new HorizontalLayout();
		
		card.setSpacing(false);
		
		top.setWidthFull();
		top.setMaxHeight("200px");
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
		
		Span subject = new Span(studySession.getSubject().getName());
		subject.getElement().getThemeList().add("badge pill");
		subject.getStyle().set("color", "#8B0836");
		subject.getStyle().setBackgroundColor("#FFF0F1");
		
		subject.addClassNames(
			    LumoUtility.TextOverflow.ELLIPSIS,
			    LumoUtility.Whitespace.NOWRAP,
			    LumoUtility.Overflow.HIDDEN
			);
		subject.getStyle().set("max-width", "350px");
		subject.getStyle().set("display", "inline-block");
		
		
		
		badges.add(type, subject);
		
		Span description = new Span(studySession.getDescription());
		
		description.addClassNames(
	            LumoUtility.Overflow.HIDDEN        //Hide text that does not fit
	            
	    );
		
		center.add(badges, description);
		
		
		//Time 
		VerticalLayout right = new VerticalLayout();
		
		Span date = new Span(this.getFormattedString(studySession.getDate().getMonth().toString() + " " + studySession.getDate().getDayOfMonth()));
		Span time = new Span(studySession.getStartTime() + " - " + studySession.getEndTime());
		
		Span availableSeats = new Span(studySession.getCountMembers() + "/" +studySession.getStudyTable().getCapacity());
		
		availableSeats.getElement().getThemeList().add(costumBadgeColor(studySession.getCountMembers(), studySession.getStudyTable().getCapacity()));
		
		right.add(date, time, availableSeats);
		

		
		//Make it so left and right pane take up 1/4 of the total lenght each
		left.setWidth("30%");
		center.setWidth("45%");
		right.setWidth("25%");
		top.add(left, center, right);
		
		
		//Student avatar Icons and join button
		AvatarGroup avatarParticipants = new AvatarGroup();
		
	    AvatarGroupItem avatar = new AvatarGroupItem(studySession.getOwner().getUsername());
	    avatar.setColorIndex(1);
	    
	    byte[] ownerPicture = this.studySession.getOwner().getProfile().getProfilePicture();
	    
	    if (ownerPicture != null && ownerPicture.length > 0) {
	        StreamResource resource = new StreamResource("avatar-" + this.studySession.getOwner().getUsername(), 
	            () -> new ByteArrayInputStream(ownerPicture));
	        
	        avatar.setImageResource(resource);
	    }
	    
	    avatarParticipants.add(avatar);
	    
	    int i = 2;
	    
	    AvatarGroupItem myAvatar = null;
	    
	    for (User partecipant: studySession.getParticipants()) {
		    String name = partecipant.getUsername();
		    AvatarGroupItem a = new AvatarGroupItem(name);
		    a.setColorIndex(i++);
		    
		    //UserProfile Image
		    
		    byte[] profilePicture = this.userService.getUserByUsername(name).getProfile().getProfilePicture();
		    
		    if (profilePicture != null && profilePicture.length > 0) {
		        StreamResource resource = new StreamResource("avatar-" + name, 
		            () -> new ByteArrayInputStream(profilePicture));
		        
		        a.setImageResource(resource);
		    }
		    
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
	    
	    Button inviteButton = new Button("Invite +");
	    inviteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
	    inviteButton.setWidth("100%");
	    
	    Button voteButton = new Button("Vote!");
	    voteButton.getStyle().set("background-color", "#FFC107"); 
	    voteButton.getStyle().set("color", "black");
	    voteButton.setWidth("100%");
	    voteButton.setIcon(VaadinIcon.STAR.create());
	    
	    
	    buttonWrapper.add(joinButton, leaveButton, inviteButton, voteButton);
	    
	    
	    //Both must exists but I just render the right one, only way to make it work in vaadin or html in general it seems
	    joinButton.setVisible(true);
	    leaveButton.setVisible(false);
	    inviteButton.setVisible(false);
	    voteButton.setVisible(false);
	    
	    if(studySession.getStatus().equals(StudySessionStatus.UPCOMING)) {
	    
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
		    
		    if(studySession.getType().equals(SessionType.PRIVATE))
		    	joinButton.setEnabled(false);
	
		    //Can't use contains because of hybernate lazy exception
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
		            
		            availableSeats.setText(studySession.getCountMembers() + "/" +studySession.getStudyTable().getCapacity());
		            availableSeats.getElement().getThemeList().clear();
		            availableSeats.getElement().getThemeList().add(costumBadgeColor(studySession.getCountMembers(), studySession.getStudyTable().getCapacity()));
		    		
		            
		        } catch (Exception e) {
		        	if(e instanceof IllegalStateException)
		        		Notification.show("Session is already full");
		        	
		        	if(e instanceof StudentBusyElsewhereException)
		        		Notification.show("You allready have a session scheduled at that time");
		        	
		        	if(e instanceof IllegalArgumentException)
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
		            
		            availableSeats.setText(studySession.getCountMembers() + "/" +studySession.getStudyTable().getCapacity());
		            availableSeats.getElement().getThemeList().clear();
		            availableSeats.getElement().getThemeList().add(costumBadgeColor(studySession.getCountMembers(), studySession.getStudyTable().getCapacity()));
		    		
		            
		        } catch (Exception e) {
		            Notification.show("Error joining session: " + e.getMessage());
		        }
		    });
		    
		    
		    //Invite button
		    
		    if(studySession.getType().equals(SessionType.PRIVATE) && studySession.getOwner().getUsername().equals(currentUsername)) {
		    	
		    	joinButton.setVisible(false);
			    leaveButton.setVisible(false);
			    inviteButton.setVisible(true);
			    
			    
			    ContextMenu userPopover = new ContextMenu();
			    userPopover.setTarget(inviteButton); 
			    userPopover.setOpenOnClick(true);  
	
			    Grid<User> userGrid = new Grid<>(User.class, false);
			    userGrid.addColumn(User::getUsername).setHeader("Username");
			    
			    //TEMP LOADING ALL USERS MAY BE CHANGED
			    userGrid.setItems(userService.getAllUsers()); 
	
			    userGrid.setWidth("300px");
			    userGrid.setHeight("500px");
	
			    userGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
	
			    
			    userGrid.addItemClickListener(event -> {
			        User selectedUser = event.getItem();
			        try {
			        
			        	this.sessionInvitationService.sendInvite(studySession, selectedUser);
			        	Notification.show("Successfully invited: " + selectedUser.getUsername());
	
			        }catch(Exception e) {
			        	
			        	if(e instanceof StudentBusyElsewhereException){
			        		Notification.show(selectedUser.getUsername() + " allready has a session scheduled at that time");
			        	}else{
			        		Notification.show("Unsuccesfully invited: " + e.getMessage());
			        	}
			        	
			        	userPopover.close();
			        	
			        }
			        
			        userPopover.close();
			        
			    });
	
			    userPopover.add(userGrid);
		    	
		    }
		    
	    }else if(studySession.getStatus().equals(StudySessionStatus.IN_PROGRESS)) {
	    	
	    	joinButton.setEnabled(false);
	    	
	    }else {	//ended session
	    	
	    	joinButton.setVisible(false);
	    	voteButton.setVisible(true);
	    	
	    	
	    	
	    	ContextMenu ratingPopup = new ContextMenu(voteButton);
	    	ratingPopup.setOpenOnClick(true);

	    	Component ratingContent = buildRatingList(ratingPopup, studySession);
	        ratingPopup.add(ratingContent);
	    	
	    }
	    
	    
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
	    
	    
	    
	    //Delete Session Button
	    
	    String currentAuthenticatedUser = this.securityService.getAuthenticatedUsername();
	    
	    // Check if UPCOMING and current user is OWNER
	    if (studySession.getStatus().equals(StudySessionStatus.UPCOMING) 
	            && studySession.getOwner().getUsername().equals(currentAuthenticatedUser)) {
	        
	        Button deleteSessionBtn = new Button(VaadinIcon.TRASH.create());
	        
	        deleteSessionBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
	        
	        // Positioning: Absolute Top Right
	        deleteSessionBtn.getStyle().set("position", "absolute");
	        deleteSessionBtn.getStyle().set("top", "10px");
	        deleteSessionBtn.getStyle().set("right", "10px");
	        deleteSessionBtn.setTooltipText("Delete Session");
	        
	        deleteSessionBtn.addClickListener(e -> {
	            try {
	                
	            	studySessionService.deleteSession(studySession); // Or deleteSession(studySession.getId()) depending on your service
	                
	                Notification.show("Study session deleted successfully")
	                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
	                
	                this.setVisible(false);
	                
	            } catch (Exception ex) {
	                Notification.show("Error deleting session: " + ex.getMessage())
	                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
	            }
	        });
	        
	        this.add(deleteSessionBtn);
	    }
	    
	    
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

	
	/**
     * Builds the list of users to rate inside the popup.
     */
    private Component buildRatingList(ContextMenu menu, StudySession session) {
        VerticalLayout listLayout = new VerticalLayout();
        listLayout.setSpacing(true);
        listLayout.setPadding(true);
        listLayout.setWidth("350px");

        String currentUsername = securityService.getAuthenticatedUsername();

        List<User> usersToRate = reviewService.getUsersToRate(session.getId(), currentUsername);

        if (usersToRate.isEmpty()) {
            Span msg = new Span("You have rated everyone!");
            msg.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);
            return new VerticalLayout(msg);
        }

        H4 title = new H4("Rate your colleagues");
        title.addClassNames(LumoUtility.Margin.NONE);
        listLayout.add(title);

        for (User targetUser : usersToRate) {
            listLayout.add(createSingleUserRatingRow(targetUser, session, menu));
        }

        return listLayout;
    }

    /**
     * Creates a single row: [Avatar] [Name] [Stars] [Submit Checkmark]
     */
    private Component createSingleUserRatingRow(User targetUser, StudySession session, ContextMenu parentMenu) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.addClassName(LumoUtility.Border.BOTTOM);

        Avatar avatar = new Avatar(targetUser.getUsername());
        avatar.addThemeVariants(AvatarVariant.LUMO_XSMALL);

        Span name = new Span(targetUser.getUsername());
        name.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.FontSize.SMALL);
        name.setWidth("80px");
        name.getStyle().set("overflow", "hidden").set("text-overflow", "ellipsis");

        final int[] selectedScore = {0}; 
        HorizontalLayout starContainer = new HorizontalLayout();
        starContainer.setSpacing(false);
        
        List<Icon> stars = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            int starValue = i;
            Icon star = VaadinIcon.STAR_O.create(); // Empty star
            star.setSize("20px");
            star.getStyle().set("cursor", "pointer");
            star.setColor("var(--lumo-contrast-30pct)");

            star.addClickListener(e -> {
                selectedScore[0] = starValue;
                updateStarVisuals(stars, starValue);
            });

            stars.add(star);
            starContainer.add(star);
        }

        Button submitBtn = new Button(VaadinIcon.CHECK.create());
        submitBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        submitBtn.setTooltipText("Submit");

        submitBtn.addClickListener(e -> {
            if (selectedScore[0] == 0) {
                Notification.show("Select a rating first", 2000, Notification.Position.MIDDLE);
                return;
            }

            try {
                reviewService.submitReview(
                    securityService.getAuthenticatedUsername(),
                    targetUser.getUsername(),
                    session.getId(),
                    selectedScore[0]
                );

                Notification.show("Rated " + targetUser.getUsername(), 3000, Notification.Position.BOTTOM_START)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                // Remove this row from UI
                row.setVisible(false);

                // Close menu if empty
                if (parentMenu.getChildren().findFirst().isEmpty()) {
                    parentMenu.close();
                }

            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        row.add(avatar, name, starContainer, submitBtn);
        return row;
    }

    /**
     * Helper to color stars Gold vs Gray
     */
    private void updateStarVisuals(List<Icon> stars, int value) {
        for (int i = 0; i < stars.size(); i++) {
            Icon s = stars.get(i);
            if (i < value) {
                s.getElement().setAttribute("icon", "vaadin:star");
                s.setColor("#fbbf24"); // Gold
            } else {
                s.getElement().setAttribute("icon", "vaadin:star-o");
                s.setColor("var(--lumo-contrast-30pct)"); // Gray
            }
        }
    }
	
}
