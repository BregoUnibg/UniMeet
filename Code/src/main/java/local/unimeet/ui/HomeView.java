package local.unimeet.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

import java.util.ArrayList;
import java.util.List;

@PageTitle("UniMeet | Dashboard")
@Route(value = "", layout = MainLayout.class) // Home Page principale
@PermitAll
public class HomeView extends VerticalLayout {

    // Contenitore per cambiare il contenuto sotto i tab
    private final Div contentContainer = new Div();

    public HomeView() {
        setSpacing(false);
        setSizeFull();
        addClassNames("home-view");

        // 1. Titolo Pagina
        HorizontalLayout header = new HorizontalLayout(new H2("Bentornato"));
        header.addClassNames(LumoUtility.Margin.MEDIUM);

        // 2. I Tabs (Sessioni vs Suggerimenti)
        Tab tabSessioni = new Tab(VaadinIcon.LIST.create(), new Span("Sessioni Recenti"));
        Tab tabSuggeriti = new Tab(VaadinIcon.LIGHTBULB.create(), new Span("Suggeriti per te"));
        
        Tabs tabs = new Tabs(tabSessioni, tabSuggeriti);
        tabs.setWidthFull();

        // 3. Logica cambio scheda
        tabs.addSelectedChangeListener(event -> {
            contentContainer.removeAll();
            if (tabs.getSelectedTab().equals(tabSessioni)) {
                contentContainer.add(createSessionLayout());
            } else {
                contentContainer.add(createSuggestedLayout());
            }
        });

        // Contenuto di default (appena apri la pagina)
        contentContainer.setSizeFull();
        contentContainer.add(createSessionLayout());

        add(header, tabs, contentContainer);
    }

    // --- LAYOUT TAB 1: GRIGLIA E RICERCA ---
    private VerticalLayout createSessionLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(true);

        // -- BARRA STRUMENTI --
        TextField search = new TextField();
        search.setPlaceholder("Cerca sessione...");
        search.setPrefixComponent(VaadinIcon.SEARCH.create());
        search.setWidth("300px");

        Button btnNew = new Button("Nuova Sessione");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNew.setIcon(VaadinIcon.PLUS.create());

        // HorizontalLayout mette gli elementi uno dopo l'altro partendo da SINISTRA.
        // Quindi search è a sinistra, il bottone subito dopo.
        HorizontalLayout toolbar = new HorizontalLayout(search, btnNew);
        toolbar.setWidthFull(); 
        // Se volessimo il bottone tutto a destra useremmo setJustifyContentMode, 
        // ma tu volevi tutto a sinistra/vicino.
        
        // -- GRIGLIA --
        Grid<MockSession> grid = new Grid<>(MockSession.class, false);
        grid.addColumn(MockSession::getTitolo).setHeader("Materia / Titolo").setAutoWidth(true);
        grid.addColumn(MockSession::getData).setHeader("Data");
        
        // Colonna stato con Badge colorato
        grid.addComponentColumn(session -> {
            Span badge = new Span(session.getStato());
            String theme = "badge";
            if (session.getStato().equalsIgnoreCase("Completato")) theme += " success";
            else if (session.getStato().equalsIgnoreCase("In Corso")) theme += " contrast";
            else theme += " error";
            
            badge.getElement().getThemeList().add(theme);
            return badge;
        }).setHeader("Stato");

        // Dati Mock (Interni, niente DB che si blocca)
        List<MockSession> items = new ArrayList<>();
        items.add(new MockSession("Analisi Matematica 1", "05/12/2023", "In Corso"));
        items.add(new MockSession("Programmazione Java", "01/12/2023", "Completato"));
        items.add(new MockSession("Basi di Dati", "28/11/2023", "Bozza"));
        grid.setItems(items);
        grid.setSizeFull();

        layout.add(toolbar, grid);
        return layout;
    }

    // --- LAYOUT TAB 2: SUGGERIMENTI (CARD) ---
    private VerticalLayout createSuggestedLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);

        layout.add(new H4("Suggerimenti basati sul tuo studio"));

        HorizontalLayout cards = new HorizontalLayout();
        cards.addClassNames(LumoUtility.Gap.MEDIUM, LumoUtility.FlexWrap.WRAP);

        cards.add(createCard("Ripasso", "Hai un esame di Analisi tra 3 giorni."));
        cards.add(createCard("Gruppo Studio", "Ci sono 2 nuovi gruppi per Java."));
        
        layout.add(cards);
        return layout;
    }

    private VerticalLayout createCard(String title, String text) {
        VerticalLayout card = new VerticalLayout();
        card.setWidth("300px");
        card.setPadding(true);
        card.setSpacing(false);
        // Stile Card
        card.getStyle().set("border", "1px solid var(--lumo-contrast-10pct)");
        card.getStyle().set("border-radius", "var(--lumo-border-radius-m)");
        card.getStyle().set("box-shadow", "var(--lumo-box-shadow-xs)");

        H4 t = new H4(title);
        t.addClassNames(LumoUtility.Margin.Top.NONE);
        Span d = new Span(text);
        d.addClassNames(LumoUtility.TextColor.SECONDARY);

        card.add(t, d);
        return card;
    }

    // Classe DTO interna per i dati finti
    public static class MockSession {
        private String titolo;
        private String data;
        private String stato;

        public MockSession(String titolo, String data, String stato) {
            this.titolo = titolo;
            this.data = data;
            this.stato = stato;
        }
        public String getTitolo() { return titolo; }
        public String getData() { return data; }
        public String getStato() { return stato; }
    }
}