package local.unimeet.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
//this class have the purpose of design how the section of the dashboard looks like
@Route(value = "gestione-sessioni", layout = MainLayout.class)
@PageTitle("Le mie Sessioni | UniMeet")
@PermitAll
public class SessionsView extends VerticalLayout {

    private final SessionService sessionService;
    private final Grid<SessionService.SessionItem> grid = new Grid<>(SessionService.SessionItem.class, false);

    private ComboBox<String> materiaSelect;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private ComboBox<String> statoSelect;

    public SessionsView(SessionService sessionService) {
        this.sessionService = sessionService;
        
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new H2("Gestisci le tue Sessioni"));

        // 1. CARD PER AGGIUNGERE NUOVA SESSIONE
        add(createCreationCard());

        // 2. LISTA SESSIONI (Con Tasto Elimina)
        add(new H4("Sessioni Attive"));
        configureGrid();
        add(grid);
        refreshGrid();
    }

    private VerticalLayout createCreationCard() {
        VerticalLayout card = new VerticalLayout();
        card.addClassNames(LumoUtility.Background.BASE, LumoUtility.Padding.LARGE, LumoUtility.BorderRadius.LARGE, LumoUtility.BoxShadow.SMALL);
        card.setSpacing(true);

        // Riga 1: Materia e Stato
        materiaSelect = new ComboBox<>("Materia");
        materiaSelect.setItems("Analisi Matematica 1", "Analisi Matematica 2", "Fisica Tecnica", "Programmazione Java", "Basi di Dati", "Architettura degli Elaboratori");
        materiaSelect.setPlaceholder("Scegli materia...");
        materiaSelect.setWidthFull();

        statoSelect = new ComboBox<>("Stato");
        statoSelect.setItems("Attivo", "Pianificato", "In Pausa");
        statoSelect.setValue("Pianificato");
        statoSelect.setWidthFull();

        HorizontalLayout row1 = new HorizontalLayout(materiaSelect, statoSelect);
        row1.setWidthFull();

        // Riga 2: Data e Ora
        datePicker = new DatePicker("Data");
        datePicker.setValue(LocalDate.now());
        datePicker.setWidthFull();

        timePicker = new TimePicker("Orario");
        timePicker.setStep(java.time.Duration.ofMinutes(15));
        timePicker.setValue(LocalTime.now());
        timePicker.setWidthFull();

        HorizontalLayout row2 = new HorizontalLayout(datePicker, timePicker);
        row2.setWidthFull();

        // Tasto Salva
        Button saveBtn = new Button("Aggiungi alla lista", VaadinIcon.PLUS.create());
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveBtn.setWidthFull();
        saveBtn.addClickListener(e -> saveSession());

        card.add(row1, row2, saveBtn);
        return card;
    }

    private void saveSession() {
        if (materiaSelect.getValue() != null && datePicker.getValue() != null && timePicker.getValue() != null) {
            sessionService.add(new SessionService.SessionItem(
                materiaSelect.getValue(), datePicker.getValue(), timePicker.getValue(), statoSelect.getValue()
            ));
            refreshGrid();
            Notification.show("Aggiunto con successo", 2000, Notification.Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            materiaSelect.clear();
        } else {
            Notification.show("Compila tutti i campi", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void configureGrid() {
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        
        // Colonna Info
        grid.addColumn(new ComponentRenderer<>(s -> {
            Span title = new Span(s.materia);
            title.addClassNames(LumoUtility.FontWeight.BOLD);
            String info = s.data.format(DateTimeFormatter.ofPattern("dd/MM")) + " ore " + s.orario;
            return new VerticalLayout(title, new Span(info));
        })).setHeader("Dettagli").setAutoWidth(true);

        // Colonna Stato
        grid.addColumn(s -> s.stato).setHeader("Stato");

        // Colonna Elimina
        grid.addComponentColumn(s -> {
            Button del = new Button(VaadinIcon.TRASH.create());
            del.addThemeVariants(ButtonVariant.LUMO_ERROR);
            del.addClickListener(e -> confirmDelete(s));
            return del;
        }).setHeader("Azioni").setWidth("100px").setFlexGrow(0);
    }

    private void confirmDelete(SessionService.SessionItem s) {
        Dialog d = new Dialog();
        d.setHeaderTitle("Elimina Sessione");
        d.add("Sei sicuro di voler eliminare " + s.materia + "?");
        Button yes = new Button("Elimina", e -> {
            sessionService.delete(s);
            refreshGrid();
            d.close();
            Notification.show("Eliminato", 2000, Notification.Position.BOTTOM_START);
        });
        yes.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        d.getFooter().add(new Button("Annulla", e -> d.close()), yes);
        d.open();
    }

    private void refreshGrid() { grid.setItems(sessionService.getAll()); }
}