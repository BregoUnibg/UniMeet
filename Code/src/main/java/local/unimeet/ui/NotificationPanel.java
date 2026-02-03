package local.unimeet.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import local.unimeet.entity.ColleagueRequest;
import local.unimeet.entity.SessionInvitation;
import local.unimeet.entity.User;
import local.unimeet.service.ColleagueRequestService;
import local.unimeet.service.SessionInvitationService;

public class NotificationPanel extends Div {

    private final VerticalLayout InviteList;
    private final SessionInvitationService sessionInvitationService;
    private final ColleagueRequestService colleagueRequestService;
    private final User currentUser;

    public NotificationPanel(User currentUser, SessionInvitationService sessionInvitationService, ColleagueRequestService colleagueRequestService) {
        
    	this.currentUser = currentUser;
        this.sessionInvitationService = sessionInvitationService;
        this.colleagueRequestService = colleagueRequestService;
        
        
        this.setVisible(false);
        this.getStyle().set("position", "fixed");
        this.getStyle().set("z-index", "999");
        this.getStyle().set("top", "60px"); 
        this.getStyle().set("right", "0px");
        this.getStyle().set("bottom", "0px");
        this.getStyle().set("width", "350px");
        this.getStyle().set("background-color", "var(--lumo-base-color)");
        this.getStyle().set("box-shadow", "-4px 0 15px rgba(0,0,0,0.1)");
        this.getStyle().set("border-left", "1px solid var(--lumo-contrast-10pct)");
        this.getStyle().set("display", "flex");
        this.getStyle().set("flex-direction", "column");

        H3 title = new H3("Invites");
        title.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        
        Button closeBtn = new Button(VaadinIcon.CLOSE.create(), e -> this.close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        
        HorizontalLayout header = new HorizontalLayout(title, closeBtn);
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.Border.BOTTOM);
        header.getStyle().set("border-bottom-color", "var(--lumo-contrast-10pct)");
        
        InviteList = new VerticalLayout();
        InviteList.setPadding(true);
        InviteList.setSpacing(true);
        InviteList.setSizeFull();
        InviteList.getStyle().set("overflow-y", "auto"); 
        
        //AddingInvites to InviteList all User's invites
        List<SessionInvitation> pendingInvitations = this.sessionInvitationService.getPendingInvitationsByInvitee(currentUser);
        
        for(SessionInvitation invitation: pendingInvitations) {
        	this.addInvitationCard(invitation);
        }
        
        //Adding ColleagueRequests to invitelist
        List<ColleagueRequest> pendingRequests = this.colleagueRequestService.getPendingReqeustByRevicer(currentUser);
        
        for(ColleagueRequest request: pendingRequests) {
        	this.addColleagueRequestCard(request);
        }
        
        this.add(header, InviteList);
    }

    // --- PUBLIC METHODS ---

    public void toggle() {
        this.setVisible(!this.isVisible());
    }
    
    public void close() {
        this.setVisible(false);
    }
    
    public void clearNotifications() {
    	InviteList.removeAll();
    }

    /**
     * Creates a visual mask for a SessionInvitation.
     * * @param invitation The entity containing the data to display.
     */
    public void addInvitationCard(SessionInvitation invitation) {
        Div card = new Div();
        card.setWidthFull();
        card.addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.BorderRadius.MEDIUM, LumoUtility.Border.ALL);
        card.getStyle().set("box-sizing", "border-box");
        card.getStyle().set("background-color", "white");
        card.getStyle().set("border-color", "var(--lumo-contrast-10pct)");
        card.getStyle().set("box-shadow", "0 2px 4px rgba(0,0,0,0.02)");

        HorizontalLayout infoLayout = new HorizontalLayout();
        infoLayout.setAlignItems(FlexComponent.Alignment.START);
        infoLayout.setWidthFull();
        
        //Academy Cap
        Div iconObj = new Div(VaadinIcon.ACADEMY_CAP.create());
        iconObj.addClassNames(LumoUtility.Background.PRIMARY_10, LumoUtility.Padding.SMALL, 
                LumoUtility.BorderRadius.LARGE, LumoUtility.Margin.Top.XSMALL);
        iconObj.getStyle().set("color", "var(--lumo-primary-color)");

        // Text Content
        VerticalLayout textLayout = new VerticalLayout();
        textLayout.setSpacing(false);
        textLayout.setPadding(false);
        
        String sessionTitle = (invitation.getSession() != null) ? invitation.getSession().getSubject().toString() : "Unknown Session";
        
        H5 title = new H5("Session Invitation");
        title.addClassNames(LumoUtility.Margin.NONE);
        
        Span sessionNameSpan = new Span(sessionTitle);
        sessionNameSpan.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.FontSize.SMALL);
        
        
        
        
        // 1. DATA E ORA
        // Uniamo LocalDate e LocalTime che sono separati nella tua Entity
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEE dd MMM");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        String datePart = invitation.getSession().getDate().format(dateFormatter);
        String startPart = invitation.getSession().getStartTime().format(timeFormatter);
        String endPart = invitation.getSession().getEndTime().format(timeFormatter);
        
        // Risultato es: "Lun 03 Feb, 14:00 - 16:00"
        String sessionTimeStr = datePart + ", " + startPart + " - " + endPart;
        
        Icon clockIcon = VaadinIcon.CLOCK.create();
        clockIcon.setSize("12px");
        clockIcon.getStyle().set("margin-right", "4px");
        
        Span sessionTimeSpan = new Span(clockIcon, new Span(sessionTimeStr));
        sessionTimeSpan.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.TextColor.SECONDARY, 
                                      LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER);

        // 2. LUOGO (Edificio e Via)
        // Uso i metodi helper della tua Entity. 
        // NOTA: Assumo che l'oggetto Building abbia un metodo .getName() o .toString() leggibile.
        // Se la tua classe Building ha un campo nome diverso (es. getNome()), cambia .getName() qui sotto.
        String buildingName = (invitation.getSession().getBuilding() != null) ? invitation.getSession().getBuilding().getName() : "Edificio N/A";
        String address = invitation.getSession().getAddress(); // Questo metodo esiste nella tua entity StudySession
        
        String locationStr = buildingName + " (" + address + ")";
        
        Icon mapIcon = VaadinIcon.MAP_MARKER.create();
        mapIcon.setSize("12px");
        mapIcon.getStyle().set("margin-right", "4px");
        
        Span sessionLocationSpan = new Span(mapIcon, new Span(locationStr));
        sessionLocationSpan.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.TextColor.SECONDARY, 
                                          LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER);
        
        
        
        
        
        
        
        // Date Formatting
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM HH:mm");
        String dateStr = invitation.getSentAtDateTime().format(formatter);
        Span timeSpan = new Span("Recived: " + dateStr);
        timeSpan.addClassNames(LumoUtility.FontSize.XXSMALL, LumoUtility.TextColor.TERTIARY);
        
        textLayout.add(title, sessionNameSpan,sessionTimeSpan, sessionLocationSpan,timeSpan);
        infoLayout.add(iconObj, textLayout);
        infoLayout.expand(textLayout); 

        // Accept Button
        Button btnAccept = new Button("Accept");
        btnAccept.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        btnAccept.addClickListener(e -> {
        	
        	this.sessionInvitationService.acceptInvite(invitation.getId());
        	Notification.show("Invite Accepted");
        	
            card.removeFromParent(); //remove card from UI
        });
        
        
        //Reject Button
        Button btnReject = new Button("Decline");
        btnReject.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        btnReject.addClickListener(e -> {
        	
        	this.sessionInvitationService.rejectInvite(invitation.getId());
        	Notification.show("Invite Rejected");
        	
            card.removeFromParent(); 
        });

        HorizontalLayout actions = new HorizontalLayout(btnReject, btnAccept);
        actions.setWidthFull();
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        actions.addClassName(LumoUtility.Margin.Top.SMALL);

        card.add(infoLayout, actions);
        InviteList.add(card);
    }
    
    public void addColleagueRequestCard(ColleagueRequest request) {
        Div card = new Div();
        card.setWidthFull();
        card.addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.BorderRadius.MEDIUM, LumoUtility.Border.ALL);
        card.getStyle().set("box-sizing", "border-box");
        card.getStyle().set("background-color", "white");
        card.getStyle().set("border-color", "var(--lumo-contrast-10pct)");
        card.getStyle().set("box-shadow", "0 2px 4px rgba(0,0,0,0.02)");

        HorizontalLayout infoLayout = new HorizontalLayout();
        infoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        infoLayout.setWidthFull();

        // Icon: User
        Div iconObj = new Div(VaadinIcon.USER.create());
        iconObj.addClassNames(LumoUtility.Background.CONTRAST_5, LumoUtility.Padding.SMALL, LumoUtility.BorderRadius.LARGE);
        iconObj.getStyle().set("color", "var(--lumo-primary-text-color)");

        // Text Content
        VerticalLayout textLayout = new VerticalLayout();
        textLayout.setSpacing(false);
        textLayout.setPadding(false);

        String senderName = (request.getSender() != null) ? request.getSender().getUsername() : "Unknown User";

        H5 title = new H5("Colleague Request");
        title.addClassNames(LumoUtility.Margin.NONE);

        Span senderSpan = new Span(senderName);
        senderSpan.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.FontSize.SMALL);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM HH:mm");
        String dateStr = "";
        if (request.getSentAtDateTime() != null) {
            dateStr = request.getSentAtDateTime().format(formatter);
        } else {
            dateStr = "Recently";
        }

        Span timeSpan = new Span("Received: " + dateStr);
        timeSpan.addClassNames(LumoUtility.FontSize.XXSMALL, LumoUtility.TextColor.TERTIARY);
        
        textLayout.add(title, senderSpan, timeSpan);
        infoLayout.add(iconObj, textLayout);
        infoLayout.expand(textLayout);

        // Accept Button
        Button btnAccept = new Button("Accept");
        btnAccept.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        btnAccept.addClickListener(e -> {
            this.colleagueRequestService.acceptRequest(request.getId());
            Notification.show("Colleague Request Accepted");
            card.removeFromParent(); 
        });

        // Reject Button
        Button btnReject = new Button("Decline");
        btnReject.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        btnReject.addClickListener(e -> {
            this.colleagueRequestService.rejectRequest(request.getId());
            Notification.show("Request Declined");
            card.removeFromParent();
        });

        HorizontalLayout actions = new HorizontalLayout(btnReject, btnAccept);
        actions.setWidthFull();
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        actions.addClassName(LumoUtility.Margin.Top.SMALL);

        card.add(infoLayout, actions);
        InviteList.add(card);
    }
    
}