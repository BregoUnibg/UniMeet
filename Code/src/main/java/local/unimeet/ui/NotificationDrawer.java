package local.unimeet.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class NotificationDrawer extends Dialog {

    private final VerticalLayout notificationList;

    public NotificationDrawer() {
        // 1. Configurazione visuale per farlo sembrare una Sidebar
        // Rimuove stili standard del dialog
        this.setHeaderTitle("Notifiche");
        this.setModal(true); 
        this.setDraggable(false);
        this.setResizable(false);
        this.setCloseOnEsc(true);
        this.setCloseOnOutsideClick(true);
        
        // STILI CSS INIETTATI VIA JAVA (Per posizionarlo a destra a tutta altezza)
        this.getElement().getStyle().set("position", "fixed");
        this.getElement().getStyle().set("right", "0");
        this.getElement().getStyle().set("top", "0");
        this.getElement().getStyle().set("bottom", "0");
        this.getElement().getStyle().set("margin", "0");
        this.getElement().getStyle().set("height", "100vh");
        this.getElement().getStyle().set("width", "350px"); // Larghezza fissa
        this.getElement().getStyle().set("border-radius", "0"); // Toglie bordi arrotondati
        this.getElement().getStyle().set("box-shadow", "var(--lumo-box-shadow-l)");

        // 2. Header personalizzato (Titolo + Bottone Chiudi)
        H3 title = new H3("Notifiche");
        title.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        
        Button closeBtn = new Button(VaadinIcon.CLOSE.create(), e -> this.close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        
        HorizontalLayout header = new HorizontalLayout(title, closeBtn);
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.addClassName(LumoUtility.Padding.MEDIUM);
        
        // 3. Contenitore scrollabile per le notifiche
        notificationList = new VerticalLayout();
        notificationList.setPadding(true);
        notificationList.setSpacing(true);
        notificationList.setSizeFull();
        // Permette lo scroll se ci sono tante notifiche
        notificationList.getStyle().set("overflow-y", "auto"); 

        // Aggiungiamo tutto al Dialog (che estende Div internamente nella nuova versione, o usa add)
        this.add(header, notificationList);
        
        // Popoliamo con dati finti per test
        addDummyNotifications();
    }

    // Metodo helper per aggiungere una notifica
    public void addNotification(String title, String description, String time) {
        Div card = new Div();
        card.setWidthFull();
        card.addClassNames(LumoUtility.Padding.SMALL, LumoUtility.Border.ALL, LumoUtility.BorderRadius.MEDIUM);
        card.getStyle().set("background-color", "var(--lumo-base-color)");
        card.getStyle().set("border-color", "var(--lumo-contrast-10pct)");

        Span titleSpan = new Span(title);
        titleSpan.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.FontSize.SMALL);
        titleSpan.getStyle().set("display", "block");

        Span descSpan = new Span(description);
        descSpan.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.TextColor.SECONDARY);
        descSpan.getStyle().set("display", "block");
        
        Span timeSpan = new Span(time);
        timeSpan.addClassNames(LumoUtility.FontSize.XXSMALL, LumoUtility.TextColor.TERTIARY);
        timeSpan.getStyle().set("display", "block");
        timeSpan.getStyle().set("margin-top", "5px");

        card.add(titleSpan, descSpan, timeSpan);
        notificationList.add(card);
    }

    private void addDummyNotifications() {
        addNotification("Lezione Spostata", "Il corso di Fisica I è stato spostato in Aula A3.", "10 min fa");
        addNotification("Nuovo Voto", "È stato pubblicato il voto di Analisi II.", "2 ore fa");
        addNotification("Promemoria", "Scadenza iscrizione esami domani.", "1 giorno fa");
        addNotification("Avviso Biblioteca", "Il libro prenotato è disponibile per il ritiro.", "2 giorni fa");
    }
}