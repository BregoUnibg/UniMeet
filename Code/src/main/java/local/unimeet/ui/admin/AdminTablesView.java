package local.unimeet.ui.admin;

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
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
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
import local.unimeet.ui.MainLayout;

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
        H3 title = new H3("Study Tables");
        title.addClassNames(LumoUtility.Margin.NONE, LumoUtility.TextColor.HEADER);

        searchField.setPlaceholder("Find table...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setWidth("500px");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> updateList());

        Button addBtn = new Button("New Table", VaadinIcon.PLUS.create());
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
        
        grid.addColumn(new ComponentRenderer<>(t -> {
            Span span = new Span(t.getNumber()+"");
            span.getStyle().set("font-weight", "bold");
            return span;
        })).setHeader("Identifier").setSortable(true).setComparator(StudyTable::getNumber).setAutoWidth(true);
        
        grid.addComponentColumn(t -> {
            Span badge = new Span(t.getCapacity() + " Seats");
            badge.getElement().getThemeList().add("badge"); 
            return badge;
        }).setHeader("Capacity").setSortable(true).setComparator(StudyTable::getCapacity).setAutoWidth(true);
        
        grid.addComponentColumn(t -> {
            if (t.getRoom() != null) {
                Span roomText = new Span("Room " + t.getRoom().getNumber());
                
                Span buildText = new Span(t.getRoom().getBuilding().getName() + " - " + t.getRoom().getBuilding().getUniversity().getName());
                buildText.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.TextColor.SECONDARY);
                
                VerticalLayout col = new VerticalLayout(roomText, buildText);
                col.setSpacing(false); col.setPadding(false);
                return col;
            }
            return new Span("-");
        })
        .setHeader("Location")
        .setAutoWidth(true)
        .setSortable(true)
        .setComparator((t1, t2) -> {
            String u1 = (t1.getRoom() != null) ? t1.getRoom().getNumber() + "" : "";
            String u2 = (t2.getRoom() != null) ? t2.getRoom().getNumber() + "" : "";
            return String.CASE_INSENSITIVE_ORDER.compare(u1, u2);
        });


        // Actions
        grid.addComponentColumn(table -> {
            Button edit = new Button(VaadinIcon.EDIT.create());
            edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            edit.addClickListener(e -> openDialog(table));

            Button delete = new Button(VaadinIcon.TRASH.create());
            delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            delete.addClickListener(e -> {
            	Dialog confirmDialog = new Dialog();
                confirmDialog.setHeaderTitle("Delete Table");
                confirmDialog.add("Are you sure to delite" + table.getNumber() +"?");
                
                Button confirmBtn = new Button("Delete", event -> {
                	try {
	                    deleteTable(table);
	                    confirmDialog.close();
                	} catch (Exception e2) {
                        // Catching generic errors
                        Notification.show("Error deleting table: " + e2.getMessage())
                                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        confirmDialog.close();
                    }
                });
                confirmBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
                Button cancelBtn = new Button("Cancel", event -> confirmDialog.close());
                confirmDialog.getFooter().add(cancelBtn, confirmBtn);
                confirmDialog.open();
            });
            return new HorizontalLayout(edit, delete);
        }).setHeader("Action");
    }

    private void updateList() {
        String filter = searchField.getValue().toLowerCase();
        grid.setItems(tableService.getTablesForUser(currentUser).stream()
            .filter(t -> String.valueOf(t.getNumber()).contains(filter) ||
                         (t.getRoom() != null && String.valueOf(t.getRoom().getNumber()).contains(filter)) ||
                         (t.getRoom() != null && t.getRoom().getBuilding().getUniversity().getName().toLowerCase().contains(filter)))
            .toList());
    }
    
    private void deleteTable(StudyTable table) {
    	tableService.deleteTable(table);
        Notification.show("Table deleted successfully").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        updateList();
    }

    private void openDialog(StudyTable table) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(table.getId() != 0 ? "Edit Table " + table.getNumber() : "New Table");
        dialog.setWidth("400px");

        VerticalLayout form = new VerticalLayout();
        IntegerField numberField = new IntegerField("Table Number");
        IntegerField capacityField = new IntegerField("Capacity");
        
        ComboBox<Room> roomSelect = new ComboBox<>("Room");
        roomSelect.setItems(roomService.getRoomsForUser(currentUser));
        roomSelect.setItemLabelGenerator(r -> "Room " + r.getNumber() + " - " + r.getBuilding().getName());
        
        numberField.setWidthFull();
        capacityField.setWidthFull();
        roomSelect.setWidthFull();

        if (table.getNumber() > 0) numberField.setValue(table.getNumber());
        if (table.getRoom() != null) roomSelect.setValue(table.getRoom());

        Button save = new Button("Save", e -> {
            if (roomSelect.getValue() == null || numberField.getValue() == null || capacityField.getValue() == null) {
                Notification.show("Please fill the entire form!"); return;
            }
            
            try {
                table.setNumber(numberField.getValue());
                table.setCapacity(capacityField.getValue());
                table.setRoom(roomSelect.getValue());
                
                tableService.saveTable(table);
                updateList();
                dialog.close();
                Notification.show("Table saved successfully!");
            } catch (Exception ex) { Notification.show("Errorer occurred while saving!"); }
        });
        
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        form.add(numberField, capacityField, roomSelect);
        dialog.add(form);
        dialog.getFooter().add(new Button("Annulla", e -> dialog.close()), save);
        dialog.open();
    }
}