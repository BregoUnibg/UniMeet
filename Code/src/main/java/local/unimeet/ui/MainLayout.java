package local.unimeet.ui;



	import com.vaadin.flow.component.applayout.AppLayout;
	import com.vaadin.flow.component.applayout.DrawerToggle;
	import com.vaadin.flow.component.html.H1;
	import com.vaadin.flow.component.icon.VaadinIcon;
	import com.vaadin.flow.component.orderedlayout.FlexComponent;
	import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
	import com.vaadin.flow.component.orderedlayout.VerticalLayout;
	import com.vaadin.flow.component.sidenav.SideNav;
	import com.vaadin.flow.component.sidenav.SideNavItem;
	import com.vaadin.flow.theme.lumo.LumoUtility;

	public class MainLayout extends AppLayout {

	    public MainLayout() {
	        createHeader();
	        createDrawer();
	    }

	    private void createHeader() {
	        H1 logo = new H1("UniMeet");
	        logo.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.MEDIUM);

	        // Il pulsante per aprire/chiudere il menu
	        DrawerToggle toggle = new DrawerToggle();

	        // Header: Toggle + Logo.
	        // NESSUNA campanella, NESSUN avatar a destra.
	        HorizontalLayout header = new HorizontalLayout(toggle, logo);
	        
	        // Allineamento verticale centrato
	        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
	        header.setWidthFull();
	        header.addClassNames(LumoUtility.Padding.Vertical.NONE, LumoUtility.Padding.Horizontal.MEDIUM);

	        addToNavbar(header);
	    }

	    private void createDrawer() {
	        SideNav nav = new SideNav();

	        // Link alla HomeView (Dashboard)
	        nav.addItem(new SideNavItem("Dashboard", HomeView.class, VaadinIcon.DASHBOARD.create()));
	        nav.addItem(new SideNavItem("Le mie Sessioni", HomeView.class, VaadinIcon.BOOK.create()));
	        nav.addItem(new SideNavItem("Impostazioni", HomeView.class, VaadinIcon.COG.create()));

	        VerticalLayout scroller = new VerticalLayout(nav);
	        addToDrawer(scroller);
	    }
	}

