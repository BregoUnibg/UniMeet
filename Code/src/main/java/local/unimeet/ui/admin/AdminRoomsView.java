package local.unimeet.ui.admin;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;

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
import local.unimeet.entity.Building;
import local.unimeet.entity.Role;
import local.unimeet.entity.Room;
import local.unimeet.entity.University;
import local.unimeet.entity.User;
import local.unimeet.security.SecurityService;
import local.unimeet.service.BuildingService;
import local.unimeet.service.RoomService;
import local.unimeet.service.UniversityService;
import local.unimeet.service.UserService;
import local.unimeet.ui.MainLayout;

@Route(value = "admin/rooms", layout = MainLayout.class)
@PageTitle("Gestione Aule")
@RolesAllowed({"ROLE_ADMIN", "ROLE_UNI_ADMIN"})
public class AdminRoomsView extends VerticalLayout {

    private final RoomService roomService;
    private final BuildingService buildingService;
    private final User currentUser;
    private final UserService userService;
    private final SecurityService securityService;
    private final UniversityService universityService;

    private Grid<Room> grid = new Grid<>(Room.class, false);
    private TextField searchField = new TextField();

    public AdminRoomsView(RoomService roomService, BuildingService buildingService, UniversityService universityService,
    		SecurityService securityService, UserService userService) {
        this.roomService = roomService;
        this.buildingService = buildingService;
        this.universityService = universityService;
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
        H3 title = new H3("Rooms");
        title.addClassNames(LumoUtility.Margin.NONE, LumoUtility.TextColor.HEADER);

        searchField.setPlaceholder("Search (e.g. 101)...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setWidth("500px");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> updateList());

        Button addBtn = new Button("New Room", VaadinIcon.PLUS.create());
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

        // Room Number
        grid.addColumn(new ComponentRenderer<>(room -> {
            Span span = new Span(room.getNumber()+"");
            span.getStyle().set("font-weight", "bold");
            return span;
        })).setHeader("Number").setSortable(true).setComparator(Room::getNumber).setAutoWidth(true);
        
        // Using a ComponentRenderer to stack Building Name and University Name vertically
        grid.addColumn(new ComponentRenderer<>(room -> {
            VerticalLayout layout = new VerticalLayout();
            layout.setPadding(false);
            layout.setSpacing(false);
            layout.setAlignItems(FlexComponent.Alignment.START);

            String buildingName = room.getBuilding() != null ? room.getBuilding().getName() : "-";
            Span buildingSpan = new Span(buildingName);
            //buildingSpan.getStyle().set("font-weight", "bold");

            String universityName = "-";
            if (room.getBuilding() != null && room.getBuilding().getUniversity() != null) {
                universityName = room.getBuilding().getUniversity().getName();
            }
            Span uniSpan = new Span(universityName);
            uniSpan.getStyle().set("font-size", "var(--lumo-font-size-xs)"); // Smaller font
            uniSpan.getStyle().set("color", "var(--lumo-secondary-text-color)"); // Lighter color

            layout.add(buildingSpan, uniSpan);
            return layout;
        }))
        .setHeader("Building")
        .setSortable(true)
        .setAutoWidth(true)
        .setComparator((r1, r2) -> {
            String u1 = (r1.getBuilding() != null) ? r1.getBuilding().getName() : "";
            String u2 = (r2.getBuilding() != null) ? r2.getBuilding().getName() : "";
            return String.CASE_INSENSITIVE_ORDER.compare(u1, u2);
        });

        // Actions Column
        grid.addComponentColumn(room -> {
            Button edit = new Button(VaadinIcon.EDIT.create());
            edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            edit.addClickListener(e -> openDialog(room));

            Button delete = new Button(VaadinIcon.TRASH.create());
            delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            delete.addClickListener(e -> {
                Dialog confirmDialog = new Dialog();
                confirmDialog.setHeaderTitle("Delete Room");
                confirmDialog.add("Are you sure to delite" + room.getNumber() +"? All associated tables will be deleted (if allowed).");
                
                Button confirmBtn = new Button("Delete", event -> {
                	try {
	                    deleteRoom(room);
	                    confirmDialog.close();
                	} catch (DataIntegrityViolationException e1) {
                		// Catching DB constraint violation
                        Notification.show("CANNOT DELETE: There are active Study Sessions or dependent data associated with this university.",
                        			5000, Notification.Position.MIDDLE)
                        			.addThemeVariants(NotificationVariant.LUMO_ERROR);
                        confirmDialog.close();
                	} catch (Exception e2) {
                        // Catching generic errors
                        Notification.show("Error deleting room: " + e2.getMessage())
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
        }).setHeader("Actions");
    }

    private void updateList() {
        
        String filter = searchField.getValue().toLowerCase();
        grid.setItems(roomService.getRoomsForUser(currentUser).stream()
                .filter(r -> String.valueOf(r.getNumber()).contains(filter) || 
                             (r.getBuilding() != null && r.getBuilding().getName().toLowerCase().contains(filter)) ||
                             (r.getBuilding().getUniversity() != null && r.getBuilding().getUniversity().getName().toLowerCase().contains(filter)))
                .toList());
    }
    
    private void deleteRoom(Room room) {
    	roomService.deleteRoom(room);
        Notification.show("Room deleted successfully").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        updateList();
    }

    private void openDialog(Room room) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(room.getId() != 0 ? "Edit Room " + room.getNumber() : "New Room");
        dialog.setWidth("400px");

        VerticalLayout form = new VerticalLayout();
        IntegerField numberField = new IntegerField("Room Number");
        
        ComboBox<University> uniSelect = new ComboBox<>("University");
        uniSelect.setItems(universityService.getAllUniversities());
        uniSelect.setItemLabelGenerator(University::getName);
        if (room.getNumber() == 0)
        		uniSelect.setValue(currentUser.getUniversity());
        
        if(currentUser.getRole() == Role.UNI_ADMIN)
        	uniSelect.setReadOnly(true);
        
        ComboBox<Building> buildingSelect = new ComboBox<>("Building");
        buildingSelect.setItemLabelGenerator(b -> b.getName() + " - " + b.getUniversity().getName());
        
        uniSelect.setWidthFull();
        buildingSelect.setWidthFull();
        numberField.setWidthFull();
        
        buildingSelect.setItems(buildingService.getBuildingsForUser(currentUser));
        
        // Handle Edit Mode (Pre-fill existing values)
        if (room.getId() != 0) {
            numberField.setValue(room.getNumber());
            if (room.getBuilding() != null) {
                // Set University first to trigger the building filter
            	uniSelect.setValue(room.getBuilding().getUniversity());
                buildingSelect.setValue(room.getBuilding());
            }
        }
        
        // Logic: When University changes, update Building list
        uniSelect.addValueChangeListener(e -> {
            buildingSelect.clear();
            if (e.getValue() != null) {
                // Fetch buildings for this specific university
                // Assumes buildingService has a method findByUniversity. 
                // If not, filter the list available to the user.
                List<Building> availableBuildings = buildingService.getBuildingsByUniversity(e.getValue().getName());
                buildingSelect.setItems(availableBuildings);
                buildingSelect.setEnabled(true);
            } else {
                buildingSelect.setEnabled(false);
            }
        });
        
        // Trigger the listener initially to populate buildings if a university is already selected
        if(uniSelect.getValue() != null) {
             List<Building> availableBuildings = buildingService.getBuildingsByUniversity(uniSelect.getValue().getName());
             buildingSelect.setItems(availableBuildings);
             if(room.getBuilding() != null) buildingSelect.setValue(room.getBuilding());
        }

        Button save = new Button("Save", e -> {
            if (buildingSelect.getValue() == null || numberField.getValue() == null) {
            	Notification.show("Please fill the entire form!"); return;
            }
            
            // Even if the UI is manipulated, we check the user rights before saving
            if (currentUser.getRole() == Role.UNI_ADMIN) {
                // Check if the selected building belongs to the admin's university
                if (!buildingSelect.getValue().getUniversity().getName().equals(currentUser.getUniversity().getName())) {
                    Notification.show("Security Error: You cannot add rooms to a University you do not manage.");
                    return;
                }
            }
            
            try {
	            room.setNumber(numberField.getValue());
	            room.setBuilding(buildingSelect.getValue());
	            
	            roomService.saveRoom(room);
	            updateList();
	            dialog.close();
	            Notification.show("Room saved successfully").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) { Notification.show("Errorer occurred while saving!"); }
        });
        
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Cancel", e -> dialog.close());

        form.add(numberField, uniSelect, buildingSelect);
        dialog.add(form);
        dialog.getFooter().add(cancel, save);
        dialog.open();
    }
}