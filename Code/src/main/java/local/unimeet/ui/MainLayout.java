package local.unimeet.ui;

import java.util.Optional;

import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;

import local.unimeet.entity.Role;
import local.unimeet.entity.User;
import local.unimeet.security.SecurityService;
import local.unimeet.ui.sessionview.SessionsView;

public class MainLayout extends AppLayout {

    private final SecurityService securityService;

    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;

        setPrimarySection(Section.DRAWER);
        createDrawer();
        createHeader();
        getElement().getThemeList().add("no-border");
    }

    private void createHeader() {
        DrawerToggle toggle = new DrawerToggle();
        Div spacer = new Div();
        spacer.setWidthFull();
        
        Button notificationBtn = new Button(LumoIcon.BELL.create());
        notificationBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        notificationBtn.getStyle().set("color", "#0f172a").set("font-size", "1.3rem");

        addToNavbar(toggle, spacer, notificationBtn);
    }

    private void createDrawer() {
        VerticalLayout drawerContent = new VerticalLayout();
        drawerContent.setSizeFull(); 
        drawerContent.setPadding(false); 
        drawerContent.setSpacing(false);
        
     
        drawerContent.getStyle().set("background-color", "#0f172a").set("color", "white");
        drawerContent.getElement().getThemeList().add("dark");
        
       
        VerticalLayout drawerTopContent = new VerticalLayout();
        drawerTopContent.setSizeFull(); 
        drawerTopContent.setWidthFull();
        drawerTopContent.setSpacing(false);
        
        Icon capIcon = VaadinIcon.ACADEMY_CAP.create();
        capIcon.setSize("32px"); capIcon.setColor("white");
        H1 appName = new H1("UniMeet");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.EXTRABOLD);
        appName.getStyle().set("margin", "0").set("color", "white");
        
        HorizontalLayout branding = new HorizontalLayout(capIcon, appName);
        branding.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        branding.setPadding(true); branding.setSpacing(true); branding.addClassName(LumoUtility.Margin.Bottom.MEDIUM);

       
        SideNav nav = new SideNav();
        
        Optional<User> maybeUser = securityService.getAuthenticatedUser();
        
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            
            // =========================================================
            //  SEZIONE ADMIN / RETTORE
            // =========================================================
            if (user.getRole() == Role.ADMIN || user.getRole() == Role.UNI_ADMIN) {
                
                nav.addItem(new SideNavItem("Dashboard", AdminHomeView.class, VaadinIcon.DASHBOARD.create()));

                nav.addItem(new SideNavItem("Gestione Edifici", AdminBuildingsView.class, VaadinIcon.BUILDING.create()));
                
                nav.addItem(new SideNavItem("Gestione Aule", AdminRoomsView.class, VaadinIcon.KEY.create()));

                nav.addItem(new SideNavItem("Gestione Tavoli", AdminTablesView.class, VaadinIcon.TABLE.create()));

                
                nav.addItem(new SideNavItem("Gestione Corsi", AdminCoursesView.class, VaadinIcon.ACADEMY_CAP.create()));

            } 
            // =========================================================
            //  SEZIONE STUDENTE
            // =========================================================
            else {
                nav.addItem(new SideNavItem("Home", HomeView.class, VaadinIcon.HOME.create()));
                nav.addItem(new SideNavItem("My Sessions", SessionsView.class, VaadinIcon.BOOK.create()));
                
            }
        }

        Div navWrapper = new Div(nav);
        navWrapper.addClassName(LumoUtility.Padding.Horizontal.MEDIUM);
        navWrapper.getStyle().set("padding", "0").set("border", "0");        
        navWrapper.setWidthFull();

        HorizontalLayout footer = createUserProfileFooter();
        
        drawerTopContent.add(branding, navWrapper);
        drawerContent.add(drawerTopContent, footer);
        drawerContent.expand(navWrapper);
        addToDrawer(drawerContent);
    }

    private HorizontalLayout createUserProfileFooter() {
        String username = securityService.getAuthenticatedUsername();
        if (username == null) username = "Ospite";
        
        Avatar avatar = new Avatar(username);
        avatar.addThemeName("xsmall");
        avatar.getStyle().set("background-color", "white").set("color", "#0f172a");
        
        RouterLink nameLink = new RouterLink(username, PersonalArea.class);
        nameLink.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.FontSize.SMALL); 
        nameLink.getStyle().set("color", "white");
        
        Span emailSpan = new Span(username + "@uni.it");
        emailSpan.addClassNames(LumoUtility.FontSize.XSMALL); 
        emailSpan.getStyle().set("color", "#e2e8f0"); 

        VerticalLayout userInfo = new VerticalLayout(nameLink, emailSpan);
        userInfo.setSpacing(false); userInfo.setPadding(false);
        
        Button logoutBtn = new Button(VaadinIcon.SIGN_OUT.create());
        logoutBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        logoutBtn.getStyle().set("color", "white");
        logoutBtn.addClickListener(e -> {
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
            UI.getCurrent().getPage().setLocation("/login");
        });

        HorizontalLayout footer = new HorizontalLayout(avatar, userInfo, logoutBtn);
        footer.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        footer.setWidthFull(); footer.addClassNames(LumoUtility.Padding.MEDIUM);
        footer.getStyle().set("background-color", "rgba(255, 255, 255, 0.05)"); 
        return footer;
    }
}