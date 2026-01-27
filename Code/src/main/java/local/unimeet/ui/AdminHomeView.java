package local.unimeet.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import local.unimeet.entity.User;
import local.unimeet.security.SecurityService;
import local.unimeet.service.BuildingService;
import local.unimeet.service.UserService;

@Route(value = "admin", layout = MainLayout.class)
@PageTitle("Admin Dashboard")
@RolesAllowed({"ROLE_ADMIN", "ROLE_UNI_ADMIN"})
public class AdminHomeView extends VerticalLayout {

    private final SecurityService securityService;
    private final BuildingService buildingService;
    private final UserService userService;
    private final User currentUser;
    
    public AdminHomeView(SecurityService securityService, 
    		BuildingService buildingService,
    		UserService userService,
    		User currentuser) {
        this.securityService = securityService;
        this.userService=userService;
         this.buildingService = buildingService;
         this.currentUser = this.userService.getUserByUsername(this.securityService.getAuthenticatedUsername());

        addClassName("admin-dashboard");
        setPadding(true);
        setSpacing(true);

        
        add(createAdminBanner());

      
        add(createStatsRow());
    }

    private Component createAdminBanner() {
        // Recuperiamo i dati
        User user = this.currentUser;
        String titleText = "Bentornato, " + user.getUsername();
        String subtitleText = (user.getUniversity() != null) 
                ? "Gestione Ateneo: " + user.getUniversity().getName()
                : "Master Admin - Accesso Completo";

        // Layout Banner
        HorizontalLayout banner = new HorizontalLayout();
        banner.setWidthFull();
        banner.setMinHeight("180px");
        banner.addClassNames(LumoUtility.Padding.XLARGE, LumoUtility.BorderRadius.LARGE, LumoUtility.AlignItems.CENTER);
        
        
        banner.getStyle().set("background", "linear-gradient(135deg, #0f172a 0%, #1e293b 100%)");
        banner.getStyle().set("color", "white");
        banner.getStyle().set("box-shadow", "0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)");
        banner.getStyle().set("position", "relative");
        banner.getStyle().set("overflow", "hidden");

        
        H1 title = new H1(titleText);
        title.addClassNames(LumoUtility.Margin.NONE, LumoUtility.FontWeight.BOLD);
        title.getStyle().set("font-size", "2.2rem").set("color", "white");

        Span subtitle = new Span(subtitleText);
        subtitle.addClassNames(LumoUtility.FontSize.LARGE);
        subtitle.getStyle().set("color", "#e0e7ff").set("font-weight", "500");

        VerticalLayout textContainer = new VerticalLayout(title, subtitle);
        textContainer.setPadding(false);
        textContainer.setSpacing(false);

        // Icona Decorativa Sfondo
        Icon decorationIcon = VaadinIcon.BUILDING_O.create();
        decorationIcon.setSize("220px");
        decorationIcon.setColor("white");
        decorationIcon.getStyle()
            .set("opacity", "0.1")
            .set("position", "absolute")
            .set("right", "-40px")
            .set("bottom", "-50px")
            .set("transform", "rotate(-10deg)");

        banner.add(textContainer, decorationIcon);
        banner.expand(textContainer);
        return banner;
    }

    private Component createStatsRow() {
        User user = this.currentUser;
        
       
        int buildingsCount = buildingService.getBuildingsForUser(user).size();
        
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.addClassNames(LumoUtility.Gap.MEDIUM, LumoUtility.FlexWrap.WRAP, LumoUtility.Margin.Vertical.MEDIUM);

        // Card 1: Edifici Gestiti
        row.add(createCard("Edifici Gestiti", String.valueOf(buildingsCount), VaadinIcon.BUILDING.create(), "primary"));
        
        
        
        return row;
    }

    private VerticalLayout createCard(String title, String value, Icon icon, String theme) {
        VerticalLayout card = new VerticalLayout();
        card.addClassNames(LumoUtility.Background.BASE, LumoUtility.BoxShadow.XSMALL, LumoUtility.BorderRadius.MEDIUM, LumoUtility.Padding.LARGE);
        card.setMinWidth("240px");
        card.setSpacing(false);

       
        if ("primary".equals(theme)) icon.getStyle().set("color", "var(--lumo-primary-color)");
        
        icon.setSize("24px");

        H3 valueText = new H3(value);
        valueText.addClassNames(LumoUtility.Margin.Top.SMALL, LumoUtility.Margin.Bottom.NONE);
        valueText.getStyle().set("font-size", "2rem");

        Span titleText = new Span(title);
        titleText.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL, LumoUtility.FontWeight.BOLD);

        card.add(icon, valueText, titleText);
        return card;
    }
}