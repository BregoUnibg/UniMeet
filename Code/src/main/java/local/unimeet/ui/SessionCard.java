package local.unimeet.ui;

import com.vaadin.flow.component.avatar.AvatarGroup;
import com.vaadin.flow.component.avatar.AvatarGroup.AvatarGroupItem;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class SessionCard extends Div{
	
	public SessionCard() {
		
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
		AvatarGroup avatarPartecipants = new AvatarGroup();
		
	    AvatarGroupItem avatar = new AvatarGroupItem("Jhon Deer");
	    avatar.setColorIndex(1);
	    avatarPartecipants.add(avatar);
	    
	    
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
		
		
	    Button joinButton = new Button("join");
	    joinButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
	    joinButton.setWidth("150px");
	    
	    avatarPartecipants.getStyle().set("margin-left", "16px");
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
	    
	    bottomMaskLeft.add(avatarPartecipants);
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

}
