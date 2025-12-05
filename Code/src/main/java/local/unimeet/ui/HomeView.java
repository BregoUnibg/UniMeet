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
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.format.DateTimeFormatter;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Dashboard | UniMeet")
@PermitAll
public class HomeView extends VerticalLayout {

    private final SessionService sessionService;

    public HomeView(SessionService sessionService) {
        this.sessionService = sessionService;
        
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
        Tab sessioni = new Tab("Le mie Sessioni");
        Tabs tabs = new Tabs(sessioni, new Tab("Suggeriti"));
        tabs.setWidthFull();
        tabs.addClassName(LumoUtility.Margin.Top.MEDIUM);

        Div container = new Div();
        // QUI CARICHIAMO SOLO LA LISTA, NIENTE TASTI ELIMINA/CREA
        container.add(createReadOnlyList());

        mainContent.add(tabs, container);
        add(mainContent);
    }

    private Grid<SessionService.SessionItem> createReadOnlyList() {
        Grid<SessionService.SessionItem> grid = new Grid<>(SessionService.SessionItem.class, false);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);
        
        grid.addColumn(new ComponentRenderer<>(session -> {
            Span title = new Span(session.materia);
            title.addClassNames(LumoUtility.FontWeight.EXTRABOLD, LumoUtility.FontSize.LARGE, LumoUtility.TextColor.HEADER);
            
            Icon clock = VaadinIcon.CLOCK.create(); clock.setSize("12px"); clock.setColor("#64748b");
            String dataStr = session.data.format(DateTimeFormatter.ofPattern("dd MMM")) + " - " + session.orario;
            Span date = new Span(clock, new Span(dataStr));
            date.addClassNames(LumoUtility.FontSize.SMALL); date.getStyle().set("color", "#64748b").set("display", "flex").set("align-items", "center").set("gap", "5px");

            VerticalLayout info = new VerticalLayout(title, date);
            info.setSpacing(false); info.setPadding(false);

            Span badge = new Span(session.stato);
            String theme = "badge";
            if(session.stato.equals("Attivo")) theme += " success primary"; else theme += " contrast";
            badge.getElement().getThemeList().add(theme);
            badge.getStyle().set("border-radius", "6px");

            HorizontalLayout row = new HorizontalLayout(info, badge);
            row.setDefaultVerticalComponentAlignment(Alignment.CENTER);
            row.setWidthFull(); row.expand(info); row.addClassName(LumoUtility.Padding.Vertical.SMALL);
            return row;
        })).setHeader("Prossime Attività"); // NESSUNA COLONNA ELIMINA QUI

        grid.setItems(sessionService.getAll());
        
        if(sessionService.getAll().isEmpty()) { grid.setHeight("100px"); }
        
        return grid;
    }

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

        H1 title = new H1("Ciao, " + username + "!");
        title.addClassNames(LumoUtility.Margin.NONE, LumoUtility.FontWeight.EXTRABOLD);
        title.getStyle().set("font-size", "2.5rem").set("color", "white");
        
        Span subtitle = new Span("Bentornato su UniMeet.");
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

    private Component createStatsRow() {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.addClassNames(LumoUtility.Gap.MEDIUM, LumoUtility.FlexWrap.WRAP, LumoUtility.Margin.Vertical.MEDIUM);
        
        int count = sessionService.getAll().size();
        row.add(createCard("Sessioni Attive", String.valueOf(count), VaadinIcon.CLOCK.create(), "primary"));
        row.add(createCard("Esami Previsti", "0", VaadinIcon.CALENDAR.create(), "contrast"));
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