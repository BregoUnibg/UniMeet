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
@Route(value = "sessions", layout = MainLayout.class)
@PageTitle("Le mie Sessioni | UniMeet")
@PermitAll
public class SessionsView extends VerticalLayout {

    
    private ComboBox<String> materiaSelect;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private ComboBox<String> statoSelect;

    public SessionsView() {
       
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new H2("Manage your Sessions"));

        add(createCreationCard());

        add(new H4("Active Sessions"));
    }

    private VerticalLayout createCreationCard() {
        VerticalLayout card = new VerticalLayout();
        card.addClassNames(LumoUtility.Background.BASE, LumoUtility.Padding.LARGE, LumoUtility.BorderRadius.LARGE, LumoUtility.BoxShadow.SMALL);
        card.setSpacing(true);

        // Riga 1: Materia e Stato
        materiaSelect = new ComboBox<>("Course");
        materiaSelect.setItems("Analisi Matematica 1", "Analisi Matematica 2", "Fisica Tecnica", "Programmazione Java", "Basi di Dati", "Architettura degli Elaboratori");
        materiaSelect.setPlaceholder("Scegli materia...");
        materiaSelect.setWidthFull();
        
        
        // What does status even mean? soon to be deleted
        statoSelect = new ComboBox<>("Status???");
        statoSelect.setItems("Attivo", "Pianificato", "In Pausa");
        statoSelect.setValue("Pianificato");
        statoSelect.setWidthFull();

        HorizontalLayout row1 = new HorizontalLayout(materiaSelect, statoSelect);
        row1.setWidthFull();

        // Date & time
        datePicker = new DatePicker("Date");
        datePicker.setValue(LocalDate.now());
        datePicker.setWidthFull();

        timePicker = new TimePicker("Time");
        timePicker.setStep(java.time.Duration.ofMinutes(15));
        timePicker.setValue(LocalTime.now());
        timePicker.setWidthFull();

        HorizontalLayout row2 = new HorizontalLayout(datePicker, timePicker);
        row2.setWidthFull();

        // Tasto Salva
        Button saveBtn = new Button("Add", VaadinIcon.PLUS.create());
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveBtn.setWidthFull();
        saveBtn.addClickListener(e -> saveSession());

        card.add(row1, row2, saveBtn);
        return card;
    }

    private void saveSession() {
        if (materiaSelect.getValue() != null && datePicker.getValue() != null && timePicker.getValue() != null) {
            Notification.show("Aggiunto con successo", 2000, Notification.Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            materiaSelect.clear();
        } else {
            Notification.show("Compila tutti i campi", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    

    private void confirmDelete() {
        Dialog d = new Dialog();
        d.setHeaderTitle("Elimina Sessione");
        Button yes = new Button("Elimina", e -> {
           
            d.close();
            Notification.show("Eliminato", 2000, Notification.Position.BOTTOM_START);
        });
        yes.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        d.getFooter().add(new Button("Annulla", e -> d.close()), yes);
        d.open();
    }

}