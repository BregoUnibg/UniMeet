package local.unimeet.ui.sessionview;

import java.util.ArrayList;

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

import local.unimeet.entity.SessionType;
import local.unimeet.entity.StudySession;
import local.unimeet.entity.User;
import local.unimeet.security.SecurityService;
import local.unimeet.service.StudySessionService;
import local.unimeet.service.UserService;

public class SessionCard extends Div {
	
	private final SecurityService securityService;
	private final UserService userService;
	private final StudySessionService studySessionService;
	private StudySession studySession;
	
	
	public SessionCard(Long studySessionId, SecurityService securityService, UserService userService, StudySessionService studySessionService) {
		
		this.securityService = securityService;
		this.userService = userService;
		this.studySessionService = studySessionService;
		
		
		this.studySession = this.studySessionService.getStudySessionById(studySessionId);

		this.setWidth("1000px");
		this.getStyle().set("background-color", "white");
		
		VerticalLayout card = new VerticalLayout();
		HorizontalLayout top = new HorizontalLayout();
		HorizontalLayout bottom = new HorizontalLayout();
		
		card.setSpacing(false);
		top.setWidthFull();
		top.setMaxHeight("150px");
		bottom.setWidthFull();
		bottom.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
		
		
		VerticalLayout left = new VerticalLayout();
		
		
		String uniName = (studySession.getUniversity() != null) ? studySession.getUniversity().getName() : "Unknown Uni";
		String buildName = (studySession.getBuilding() != null) ? studySession.getBuilding().getName() : "Unknown Building";
		String roomNum = (studySession.getRoom() != null) ? String.valueOf(studySession.getRoom().getNumber()) : "?";
		String addr = (studySession.getAddress() != null) ? studySession.getAddress() : "";

		Span university = new Span(uniName);
		Span buildingAndRoom = new Span(buildName + " " + roomNum);
		Span address = new Span(addr);
		
		left.add(university, buildingAndRoom, address);
		
		VerticalLayout center = new VerticalLayout();
		HorizontalLayout badges = new HorizontalLayout();
		
		Span type = new Span(getFormattedSessionType(studySession.getType()));
		type.getElement().getThemeList().add("badge pill");
		
		// Recupero Nome Corso 
		String courseName = "Nessun Corso";
		if (studySession.getCourse() != null) {
			courseName = studySession.getCourse().getName();
		}
		
		Span subject = new Span(courseName);
		subject.getElement().getThemeList().add("badge pill");
		subject.getStyle().set("color", "#8B0836");
		subject.getStyle().setBackgroundColor("#FFF0F1");
		
		badges.add(type, subject);
		
		Span description = new Span(studySession.getDescription());
		description.addClassNames(LumoUtility.Overflow.HIDDEN);
		center.add(badges, description);
		
		// ORARIO
		VerticalLayout right = new VerticalLayout();
		
		String dateStr = studySession.getDate() != null ? studySession.getDate().toString() : "No Date";
		Span date = new Span(dateStr);
		Span time = new Span(studySession.getTimeStart() + " - " + studySession.getTimeEnd());
		
		Span availableSeats = new Span(studySession.getCountMembers() + "/6");
		availableSeats.getElement().getThemeList().add(costumBadgeColor(studySession.getCountMembers(), 6));
		
		right.add(date, time, availableSeats);
		
		
		left.setWidth("25%");
		center.setWidth("50%");
		right.setWidth("25%");
		top.add(left, center, right);
		
		//PARTEICPANTI
		AvatarGroup avatarPartecipants = new AvatarGroup();
		
		String ownerName = studySession.getOwner() != null ? studySession.getOwner().getUsername() : "Unknown";
		AvatarGroupItem avatar = new AvatarGroupItem(ownerName);
		avatar.setColorIndex(1);
		avatarPartecipants.add(avatar);
		
		int i = 2;
		AvatarGroupItem myAvatar = null;
		
		if(studySession.getPartecipants() != null) {
			for (User partecipant: studySession.getPartecipants()) {
				String name = partecipant.getUsername();
				AvatarGroupItem a = new AvatarGroupItem(name);
				a.setColorIndex(i++);
				avatarPartecipants.add(a);
				
				if(this.securityService.getAuthenticatedUsername().equals(name)){
					myAvatar = a;
				}
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
		
		
		joinButton.setVisible(true);
		leaveButton.setVisible(false);
		
		String currentUsername = this.securityService.getAuthenticatedUsername();
		
		// Se sono l'owner, disabilito tutto
		if(ownerName.equals(currentUsername)) {
			joinButton.setEnabled(false);
			joinButton.setText("Owned");
			joinButton.getStyle().setBackground("Gray");
			leaveButton.setEnabled(false);
			leaveButton.setText("Owned");
			leaveButton.getStyle().setBackground("Gray");
		} else if(myAvatar != null){ // Se sono già partecipante
			joinButton.setVisible(false);
			leaveButton.setVisible(true);
		}
		
		final int nextColor = i;
		if(myAvatar == null) {
			myAvatar = new AvatarGroupItem(currentUsername);
			myAvatar.setColorIndex(nextColor); 
		}
		final AvatarGroupItem myAvatarWrapper = myAvatar;
		
		
		joinButton.addClickListener(event -> {
			try {
				studySessionService.addPartecipant(studySession, currentUsername);
				joinButton.setVisible(false);
				leaveButton.setVisible(true);
				Notification.show("Joined successfully!");
				avatarPartecipants.add(myAvatarWrapper);
				updateSeatsBadge(availableSeats, studySession.getCountMembers() + 1, 6);
			} catch (Exception e) {
				Notification.show("Error: " + e.getMessage());
			}
		});
		
		leaveButton.addClickListener(event -> {
			try {
				studySessionService.removePartecipant(studySession, currentUsername);
				joinButton.setVisible(true);
				leaveButton.setVisible(false);
				Notification.show("Left successfully!");
				
				updateSeatsBadge(availableSeats, studySession.getCountMembers() - 1, 6);
			} catch (Exception e) {
				Notification.show("Error: " + e.getMessage());
			}
		});
		
		
		avatarPartecipants.getStyle().set("margin-left", "16px");
		joinButton.getStyle().set("margin-left", "16px");
		
		VerticalLayout bottomMaskLeft = new VerticalLayout();
		VerticalLayout bottomMaskCenter = new VerticalLayout();
		VerticalLayout bottomMaskRight = new VerticalLayout();
		
		bottomMaskLeft.setWidth("25%"); bottomMaskCenter.setWidth("50%"); bottomMaskRight.setWidth("25%");
		bottomMaskLeft.setPadding(false); bottomMaskCenter.setPadding(false); bottomMaskRight.setPadding(false);
		
		bottomMaskLeft.add(avatarPartecipants);
		bottomMaskRight.add(buttonWrapper);
		
		bottom.add(bottomMaskLeft, bottomMaskCenter, bottomMaskRight);
		card.add(top, bottom);
		this.add(card);
		
		this.addClassNames(LumoUtility.BoxShadow.SMALL, LumoUtility.Padding.XSMALL, LumoUtility.Margin.MEDIUM);
		this.getStyle().set("border-radius", "20px");
	}
	
	private String getFormattedSessionType(SessionType ss) {
		return getFormattedString(ss.name());
	}
	
	private String getFormattedString(String string) {
		String [] words = string.split("_");
		String output = "";
		for(String word: words) {
			output += word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase() + " ";
		}
		return output.trim();
	}
	
	private String costumBadgeColor(int members, int max) {
		if(members >= max) return "badge error";
		if((double)members/max >= 0.5) return "badge warning";
		return "badge success";
	}
	
	private void updateSeatsBadge(Span span, int current, int max) {
		span.setText(current + "/6");
		span.getElement().getThemeList().clear();
		span.getElement().getThemeList().add(costumBadgeColor(current, max));
	}
}