package local.unimeet.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import local.unimeet.entity.Room;
import local.unimeet.entity.StudyTable;
import local.unimeet.entity.User;
import local.unimeet.security.SecurityService;
import local.unimeet.service.RoomService;
import local.unimeet.service.StudyTableService;
import local.unimeet.service.UserService;

@Route(value = "admin/tables", layout = MainLayout.class)
@PageTitle("Gestione Tavoli")
@RolesAllowed({"ROLE_ADMIN", "ROLE_UNI_ADMIN"})
public class AdminTablesView extends VerticalLayout {

    private final StudyTableService tableService;
    private final RoomService roomService;
    private final User currentUser;
    private final UserService userService;
    private final SecurityService securityService;

    private Grid<StudyTable> grid = new Grid<>(StudyTable.class, false);
    private TextField searchField = new TextField();

    public AdminTablesView(StudyTableService tableService, 
    	 RoomService roomService,
    		 SecurityService securityService,
     		UserService userService) {
        this.tableService = tableService;
        this.roomService = roomService;
        this.userService=userService;
        this.securityService=securityService;
        this.currentUser = this.userService.getUserByUsername(this.securityService.getAuthenticatedUsername());

        setSizeFull();
        addClassNames("list-view", LumoUtility.Padding.MEDIUM, LumoUtility.Background.CONTRAST_5);

        configureGrid();
        add(createToolbar(), createGridContainer());
        updateList();
    }
    
    private HorizontalLayout createToolbar() {
        H3 title = new H3("Tavoli Studio");
        title.addClassNames(LumoUtility.Margin.NONE, LumoUtility.TextColor.HEADER);

        searchField.setPlaceholder("Cerca (es. Tavolo 1)...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> updateList());

        Button addBtn = new Button("Nuovo Tavolo", VaadinIcon.PLUS.create());
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addBtn.addClickListener(e -> openDialog(new StudyTable()));

        HorizontalLayout toolbar = new HorizontalLayout(title, searchField, addBtn);
        toolbar.setWidthFull();
        toolbar.addClassNames(LumoUtility.Padding.Bottom.SMALL);
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        return toolbar;
    }
    
    private Div createGridContainer() {
        Div wrapper = new Div(grid);
        wrapper.setSizeFull();
        wrapper.addClassNames(LumoUtility.Background.BASE, LumoUtility.BorderRadius.LARGE, 
                              LumoUtility.BoxShadow.SMALL, LumoUtility.Overflow.HIDDEN);
        return wrapper;
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);

        // Numero Tavolo Con prefisso "Tavolo"
        grid.addColumn(t -> "Tavolo. " + t.getNumber()).setHeader("Identificativo").setAutoWidth(true).setSortable(true);
        
        // Posti Badge Blu
        grid.addComponentColumn(t -> {
            Span badge = new Span(t.getCapacity() + " Posti");
            badge.getElement().getThemeList().add("badge"); 
            return badge;
        }).setHeader("Capienza").setAutoWidth(true);
        
        // Posizione: Aula + Edificio
        grid.addComponentColumn(t -> {
            if (t.getRoom() != null) {
                Span roomText = new Span("Aula " + t.getRoom().getNumber());
                roomText.addClassNames(LumoUtility.FontWeight.BOLD);
                
                Span buildText = new Span(t.getRoom().getBuilding().getName());
                buildText.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.TextColor.SECONDARY);
                
                VerticalLayout col = new VerticalLayout(roomText, buildText);
                col.setSpacing(false); col.setPadding(false);
                return col;
            }
            return new Span("-");
        }).setHeader("Posizione").setAutoWidth(true);

        // Azioni
        grid.addComponentColumn(table -> {
            Button edit = new Button(VaadinIcon.EDIT.create());
            edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            edit.addClickListener(e -> openDialog(table));

            Button delete = new Button(VaadinIcon.TRASH.create());
            delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            delete.addClickListener(e -> {
                tableService.deleteTable(table);
                updateList();
                Notification.show("Tavolo eliminato");
            });
            return new HorizontalLayout(edit, delete);
        }).setHeader("Azioni");
    }

    private void updateList() {
        String filter = searchField.getValue().toLowerCase();
        grid.setItems(tableService.getTablesForUser(currentUser).stream()
            .filter(t -> String.valueOf(t.getNumber()).contains(filter) ||
                         (t.getRoom() != null && String.valueOf(t.getRoom().getNumber()).contains(filter)))
            .toList());
    }

    private void openDialog(StudyTable table) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Modifica Tavolo");

        VerticalLayout form = new VerticalLayout();
        IntegerField numberField = new IntegerField("Numero Tavolo");
        IntegerField capacityField = new IntegerField("Posti Disponibili");
        
        ComboBox<Room> roomSelect = new ComboBox<>("Seleziona Aula");
        roomSelect.setItems(roomService.getRoomsForUser(currentUser));
        roomSelect.setItemLabelGenerator(r -> "Aula " + r.getNumber() + " - " + r.getBuilding().getName());
        
        numberField.setWidthFull(); capacityField.setWidthFull(); roomSelect.setWidthFull();

        if (table.getNumber() > 0) numberField.setValue(table.getNumber());
        if (table.getRoom() != null) roomSelect.setValue(table.getRoom());

        Button save = new Button("Salva", e -> {
            if (roomSelect.getValue() == null || numberField.getValue() == null || capacityField.getValue() == null) {
                Notification.show("Compila tutti i campi!"); return;
            }
            try {
                table.setNumber(numberField.getValue());
                table.setCapacity(capacityField.getValue());
                table.setRoom(roomSelect.getValue());
                tableService.saveTable(table);
                updateList();
                dialog.close();
                Notification.show("Salvato!");
            } catch (Exception ex) { Notification.show("Errore: Numero duplicato!"); }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Annulla", e -> dialog.close());

        form.add(numberField, capacityField, roomSelect);
        dialog.add(form);
        dialog.getFooter().add(cancel, save);
        dialog.open();
    }
}