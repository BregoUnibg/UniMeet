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
import local.unimeet.entity.Building;
import local.unimeet.entity.Room;
import local.unimeet.entity.User;
import local.unimeet.security.SecurityService;
import local.unimeet.service.BuildingService;
import local.unimeet.service.RoomService;

@Route(value = "admin/rooms", layout = MainLayout.class)
@PageTitle("Gestione Aule")
@RolesAllowed({"ROLE_ADMIN", "ROLE_UNI_ADMIN"})
public class AdminRoomsView extends VerticalLayout {

    private final RoomService roomService;
    private final BuildingService buildingService;
    private final User currentUser;

    private Grid<Room> grid = new Grid<>(Room.class, false);
    private TextField searchField = new TextField();

    public AdminRoomsView(RoomService roomService, BuildingService buildingService, SecurityService securityService) {
        this.roomService = roomService;
        this.buildingService = buildingService;
        this.currentUser = securityService.getAuthenticatedUser().orElseThrow();

        setSizeFull();
        addClassNames("list-view", LumoUtility.Padding.MEDIUM, LumoUtility.Background.CONTRAST_5);

        configureGrid();
        add(createToolbar(), createGridContainer());
        updateList();
    }
    
    private HorizontalLayout createToolbar() {
        H3 title = new H3("Aule");
        title.addClassNames(LumoUtility.Margin.NONE, LumoUtility.TextColor.HEADER);

        searchField.setPlaceholder("Cerca (es. 101)...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> updateList());

        Button addBtn = new Button("Nuova Aula", VaadinIcon.PLUS.create());
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addBtn.addClickListener(e -> openDialog(new Room()));

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

        // Numero Aula
        grid.addColumn(Room::getNumber).setHeader("Numero").setAutoWidth(true).setSortable(true);
        
        // Capienza fatta con  Badge Verde(se si vuole si può cambiare)
        grid.addComponentColumn(room -> {
            Span badge = new Span(String.valueOf(room.getCapacity()));
            badge.getElement().getThemeList().add("badge success"); // Colore verde
            badge.setTitle("Posti a sedere");
            return badge;
        }).setHeader("Capienza").setAutoWidth(true);

       //gestisco Edificio
        grid.addColumn(r -> r.getBuilding() != null ? r.getBuilding().getName() : "-").setHeader("Edificio").setAutoWidth(true);

        
        grid.addComponentColumn(room -> {
            Button edit = new Button(VaadinIcon.EDIT.create());
            edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            edit.addClickListener(e -> openDialog(room));

            Button delete = new Button(VaadinIcon.TRASH.create());
            delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            delete.addClickListener(e -> {
                Dialog confirmDialog = new Dialog();
                confirmDialog.setHeaderTitle("Elimina Aula");
                confirmDialog.add("Confermi l'eliminazione dell'Aula " + room.getNumber() + "?");
                
                Button confirmBtn = new Button("Elimina", event -> {
                    roomService.deleteRoom(room);
                    updateList();
                    Notification.show("Aula eliminata");
                    confirmDialog.close();
                });
                confirmBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
                
                Button cancelBtn = new Button("Annulla", event -> confirmDialog.close());
                confirmDialog.getFooter().add(cancelBtn, confirmBtn);
                confirmDialog.open();
            });

            return new HorizontalLayout(edit, delete);
        }).setHeader("Azioni");
    }

    private void updateList() {
        
        String filter = searchField.getValue().toLowerCase();
        grid.setItems(roomService.getRoomsForUser(currentUser).stream()
                .filter(r -> String.valueOf(r.getNumber()).contains(filter) || 
                             (r.getBuilding() != null && r.getBuilding().getName().toLowerCase().contains(filter)))
                .toList());
    }

    private void openDialog(Room room) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(room.getId() == null ? "Nuova Aula" : "Modifica Aula " + room.getNumber());

        VerticalLayout form = new VerticalLayout();
        IntegerField numberField = new IntegerField("Numero Aula");
        IntegerField capacityField = new IntegerField("Capienza");
        ComboBox<Building> buildingSelect = new ComboBox<>("Edificio");
        
        buildingSelect.setItems(buildingService.getBuildingsForUser(currentUser));
        buildingSelect.setItemLabelGenerator(Building::getName);
        
        numberField.setWidthFull(); capacityField.setWidthFull(); buildingSelect.setWidthFull();

        if (room.getNumber() > 0) numberField.setValue(room.getNumber());
        if (room.getCapacity() > 0) capacityField.setValue(room.getCapacity());
        if (room.getBuilding() != null) buildingSelect.setValue(room.getBuilding());

        Button save = new Button("Salva", e -> {
            if (buildingSelect.getValue() == null || numberField.getValue() == null) {
                Notification.show("Compila Edificio e Numero!"); return;
            }
            try {
                room.setNumber(numberField.getValue());
                room.setCapacity(capacityField.getValue() != null ? capacityField.getValue() : 0);
                room.setBuilding(buildingSelect.getValue());
                roomService.saveRoom(room);
                updateList();
                dialog.close();
                Notification.show("Salvato!");
            } catch (Exception ex) { Notification.show("Errore: Numero già esistente!"); }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Annulla", e -> dialog.close());

        form.add(numberField, capacityField, buildingSelect);
        dialog.add(form);
        dialog.getFooter().add(cancel, save);
        dialog.open();
    }
}