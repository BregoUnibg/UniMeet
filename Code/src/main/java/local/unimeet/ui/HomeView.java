package local.unimeet.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.format.DateTimeFormatter;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Home")
@PermitAll
public class HomeView extends VerticalLayout {


    public HomeView() {
        
        addClassName("dashboard-view");
        setPadding(true); 
        setSpacing(true);

        // Banner Premium
        add(createPremiumBanner());

        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setPadding(false); 
        mainContent.setSpacing(true);

        // Stats
        mainContent.add(createStatsRow());

        // Tabs
        
        TabSheet tabs = new TabSheet();
        
        Div mySessionContent = new Div();
        Div suggestedSessionContent = new Div();
        
        tabs.add("My Sessions", mySessionContent);
        tabs.add("Suggested Sessions", suggestedSessionContent);
        
        //Just a test to see how cards look like
        mySessionContent.add(new SessionCard()); 
        mySessionContent.add(new SessionCard()); 
        suggestedSessionContent.add(new SessionCard());
        
        tabs.setWidthFull();
        tabs.addClassName(LumoUtility.Margin.Top.MEDIUM);

 
        mainContent.add(tabs);
        add(mainContent);
    }

    
//here it's created the banner in the homeview
    private Component createPremiumBanner() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(username == null || username.isEmpty()) username = "Studente";
        else username = username.substring(0, 1).toUpperCase() + username.substring(1);

        HorizontalLayout banner = new HorizontalLayout();
        banner.setWidthFull(); banner.setMinHeight("180px"); 
        banner.addClassNames(LumoUtility.Padding.XLARGE, LumoUtility.BorderRadius.LARGE, LumoUtility.AlignItems.CENTER);
        banner.getStyle().set("background", "linear-gradient(135deg, #0f172a 0%, #1e293b 100%)");
        banner.getStyle().set("color", "white");
        banner.getStyle().set("box-shadow", "inset 0 1px 1px rgba(255,255,255,0.2), 0 10px 25px -5px rgba(0,0,0,0.3)");
        banner.getStyle().set("position", "relative"); banner.getStyle().set("overflow", "hidden");

        H1 title = new H1("Good to see you, " + username + "!");
        title.addClassNames(LumoUtility.Margin.NONE, LumoUtility.FontWeight.BOLD);
        title.getStyle().set("font-size", "2.5rem").set("color", "white");
        
        Span subtitle = new Span("Welcome back on UniMeet.");
        subtitle.addClassNames(LumoUtility.FontSize.LARGE);
        subtitle.getStyle().set("color", "white").set("font-weight", "500");

        VerticalLayout textContainer = new VerticalLayout(title, subtitle);
        textContainer.setPadding(false); textContainer.setSpacing(false);

        Icon decorationIcon = VaadinIcon.DIPLOMA_SCROLL.create();
        decorationIcon.setSize("200px"); decorationIcon.setColor("white");
        decorationIcon.getStyle().set("opacity", "0.1").set("position", "absolute").set("right", "-20px").set("bottom", "-30px").set("transform", "rotate(-15deg)");

        banner.add(textContainer, decorationIcon); banner.expand(textContainer);
        return banner;
    }

    //here it's created the banner where we keep track of the users profile statistics
    private Component createStatsRow() {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.addClassNames(LumoUtility.Gap.MEDIUM, LumoUtility.FlexWrap.WRAP, LumoUtility.Margin.Vertical.MEDIUM);
        
        row.add(createCard("Active Sessions", String.valueOf(0), VaadinIcon.CLOCK.create(), "primary"));
        return row;
    }

    
    private VerticalLayout createCard(String l, String v, Component i, String c) {
        VerticalLayout card = new VerticalLayout();
        card.addClassNames(LumoUtility.Background.BASE, LumoUtility.BoxShadow.XSMALL, LumoUtility.BorderRadius.MEDIUM, LumoUtility.Padding.MEDIUM);
        card.setMinWidth("220px"); card.setSpacing(false);
        if(c.equals("primary")) i.getStyle().set("color", "var(--lumo-primary-color)");
        if(c.equals("contrast")) i.getStyle().set("color", "#0f172a");
        card.add(i, new H3(v), new Span(l));
        return card;
    }
}