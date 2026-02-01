package local.unimeet.ui;

import java.io.ByteArrayInputStream;

import org.springframework.security.core.context.SecurityContextHolder;
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
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;

import local.unimeet.entity.Role;
import local.unimeet.entity.User;
import local.unimeet.entity.UserProfile;
import local.unimeet.repository.UserRepository;
import local.unimeet.security.SecurityService;
import local.unimeet.service.ProfileService;
import local.unimeet.service.UserService;
import local.unimeet.ui.sessionview.SessionsView;

public class MainLayout extends AppLayout {
	
	SecurityService securityService;
	UserService userService;
	ProfileService profileService;
	UserRepository userRepository;
	
	private NotificationDrawer notificationDrawer;
	
    public MainLayout(SecurityService securityService, UserService userService, UserRepository userRepository, ProfileService profileService){
    	
    	this.securityService = securityService;
    	this.userService = userService;
    	this.profileService = profileService;
    	this.userRepository = userRepository;
    	
    	this.notificationDrawer = new NotificationDrawer();
    	
    	//Makes left navbar domnant over header
    	setPrimarySection(Section.DRAWER);
    	
    	createDrawer();
        createHeader();
        getElement().getThemeList().add("no-border");
    }

    //create the header when are sitated the bell the aeroplano and the tre liniette for opening and closing the left column
     
    private void createHeader() {
        DrawerToggle toggle = new DrawerToggle();
        Div spacer = new Div();
        spacer.setWidthFull();
        
        // Bell Icon
        Button notificationBtn = new Button(LumoIcon.BELL.create());
        notificationBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        notificationBtn.getStyle().set("color", "#0f172a").set("font-size", "1.3rem");
        notificationBtn.addClickListener(e -> notificationDrawer.open());
        
        addToNavbar(toggle, spacer, notificationBtn);
        
        
    }

    //create the icon where the user can navigate and do the various activity offered by the app 
    
    private void createDrawer() {
        VerticalLayout drawerContent = new VerticalLayout();
        drawerContent.setSizeFull(); 
        drawerContent.setPadding(false); 
        drawerContent.setSpacing(false);
        drawerContent.getStyle().set("background-color", "#0f172a").set("color", "white");
        drawerContent.getElement().getThemeList().add("dark");
        
        //New drawer to will with elements which will have padding in contrast to the footer
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

        Div navWrapper = new Div();
        
        User currentUser = userService.getUserByUsername(securityService.getAuthenticatedUsername());
        
        if (currentUser != null) {
            // =========================================================
            //  User pages
            // =========================================================
        	SideNav userNav = new SideNav("Menu Principale"); // Il titolo appare sopra gli elementi
            userNav.setCollapsible(true);
            userNav.addItem(new SideNavItem("Home", HomeView.class, VaadinIcon.HOME.create()));
            userNav.addItem(new SideNavItem("My Sessions", SessionsView.class, VaadinIcon.BOOK.create()));
            userNav.addItem(new SideNavItem("Search", SearchView.class, VaadinIcon.SEARCH.create()));
            
            navWrapper.add(userNav);
            
            // =========================================================
            //  Admin pages
            // =========================================================
            if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.UNI_ADMIN) {
            	SideNav adminNav = new SideNav("Amministrazione");
                adminNav.setCollapsible(true);
                adminNav.setExpanded(false);
                
                adminNav.addItem(new SideNavItem("Dashboard", AdminHomeView.class, VaadinIcon.DASHBOARD.create()));
                adminNav.addItem(new SideNavItem("Buildings", AdminBuildingsView.class, VaadinIcon.BUILDING.create()));
                adminNav.addItem(new SideNavItem("Room", AdminRoomsView.class, VaadinIcon.KEY.create()));
                adminNav.addItem(new SideNavItem("Tables", AdminTablesView.class, VaadinIcon.TABLE.create()));
                adminNav.addItem(new SideNavItem("Courses", AdminCoursesView.class, VaadinIcon.ACADEMY_CAP.create()));

                navWrapper.add(adminNav);
            } 
        }
        
        //Needeed to fill up the navbar and make Items width same size as the navbar
        navWrapper.addClassName(LumoUtility.Padding.Horizontal.MEDIUM);
        navWrapper.getStyle().set("padding", "0");
        navWrapper.getStyle().set("border", "0");        
        navWrapper.setWidthFull();

        HorizontalLayout footer = createUserProfileFooter();
        
        drawerTopContent.add(branding, navWrapper);
        drawerContent.add(drawerTopContent, footer);
        drawerContent.expand(navWrapper);
        addToDrawer(drawerContent);
    }

    //create the avatar and the email in the left column 
    
    private HorizontalLayout createUserProfileFooter() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username == null) username = "Studente";
        Avatar avatar = new Avatar(username);
        avatar.addThemeName("xsmall");
        avatar.getStyle().set("background-color", "white").set("color", "#0f172a");
        if (username != null) {
        	User currentUser = userRepository.findById(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            UserProfile currentProfile = profileService.getOrCreateProfile(currentUser);
        	if (currentProfile.getProfilePicture() != null && currentProfile.getProfilePicture().length > 0) {
                StreamResource resource = new StreamResource("avatar", 
                    () -> new ByteArrayInputStream(currentProfile.getProfilePicture()));
                avatar.setImageResource(resource);
            }
        }       
        
        RouterLink nameLink = new RouterLink(username, PersonalArea.class);
        nameLink.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.FontSize.SMALL); nameLink.getStyle().set("color", "white");
        
        Span emailSpan = new Span(username + "@uni.it");
        emailSpan.addClassNames(LumoUtility.FontSize.XSMALL); emailSpan.getStyle().set("color", "#e2e8f0"); 

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