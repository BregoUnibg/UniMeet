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
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import local.unimeet.entity.Building;
import local.unimeet.entity.Role;
import local.unimeet.entity.University;
import local.unimeet.entity.User;
import local.unimeet.repository.UniversityRepository;
import local.unimeet.security.SecurityService;
import local.unimeet.service.BuildingService;
import local.unimeet.service.UniversityService;
import local.unimeet.service.UserService;

@Route(value = "admin/buildings", layout = MainLayout.class)
@PageTitle("Gestione Edifici")
@RolesAllowed({"ROLE_ADMIN", "ROLE_UNI_ADMIN"})
public class AdminBuildingsView extends VerticalLayout {

    private final BuildingService buildingService;
    private final UniversityService universityService;
    private final User currentUser;
    private final UserService userService;
    private final SecurityService securityService;

    private Grid<Building> grid = new Grid<>(Building.class, false);
    private TextField searchField = new TextField();

    public AdminBuildingsView(BuildingService buildingService,
                              UniversityService universityService,
                              SecurityService securityService,
                              UserService userService) {
        this.buildingService = buildingService;
        this.universityService = universityService; 
        this.userService= userService;
        this.securityService=securityService;

        this.currentUser = this.userService.getUserByUsername(this.securityService.getAuthenticatedUsername());
        setSizeFull();
        addClassNames("list-view", LumoUtility.Padding.MEDIUM, LumoUtility.Background.CONTRAST_5); 

        configureGrid();
        
        // Costruiamo il Layout principale
        add(createToolbar(), createGridContainer());
        
        updateList();
    }

    private HorizontalLayout createToolbar() {
        // Titolo a sinistra
        H3 title = new H3("Edifici");
        title.addClassNames(LumoUtility.Margin.NONE, LumoUtility.TextColor.HEADER);

        // Ricerca e Bottone a destra
        searchField.setPlaceholder("Cerca edificio...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> updateList());

        Button addBtn = new Button("Nuovo Edificio", VaadinIcon.PLUS.create());
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addBtn.addClickListener(e -> openDialog(new Building()));

        HorizontalLayout toolbar = new HorizontalLayout(title, searchField, addBtn);
        toolbar.setWidthFull();
        toolbar.addClassNames(LumoUtility.Padding.Bottom.SMALL);
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        
       
        title.setVisible(true); 

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

        // Colonna Nome (Grassetto)
        grid.addComponentColumn(b -> {
            Span name = new Span(b.getName());
            name.addClassNames(LumoUtility.FontWeight.BOLD);
            return name;
        }).setHeader("Nome").setAutoWidth(true);

        // Colonna Indirizzo (Grigio)
        grid.addComponentColumn(b -> {
            Span address = new Span(b.getAddress());
            address.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);
            Icon icon = VaadinIcon.MAP_MARKER.create();
            icon.setSize("12px");
            icon.addClassName(LumoUtility.TextColor.TERTIARY);
            
            HorizontalLayout row = new HorizontalLayout(icon, address);
            row.setAlignItems(Alignment.CENTER);
            row.setSpacing(false);
            return row;
        }).setHeader("Indirizzo").setAutoWidth(true);

        // Colonna Università (Badge)
        if (currentUser.getRole() == Role.ADMIN) {
            grid.addComponentColumn(b -> {
                String uniName = b.getUniversity() != null ? b.getUniversity().getName() : "-";
                Span badge = new Span(uniName);
                badge.getElement().getThemeList().add("badge contrast");
                return badge;
            }).setHeader("Università").setAutoWidth(true);
        }

       
        grid.addComponentColumn(building -> {
            Button edit = new Button(VaadinIcon.EDIT.create());
            edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            edit.addClickListener(e -> openDialog(building));

            Button delete = new Button(VaadinIcon.TRASH.create());
            delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            delete.addClickListener(e -> {
                Dialog confirmDialog = new Dialog();
                confirmDialog.setHeaderTitle("Elimina Edificio");
                confirmDialog.add("Sei sicuro? Verranno eliminate tutte le aule e i tavoli associati.");
                Button confirmBtn = new Button("Elimina", event -> {
                    buildingService.delete(building);
                    updateList();
                    Notification.show("Edificio eliminato.");
                    confirmDialog.close();
                });
                confirmBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
                Button cancelBtn = new Button("Annulla", event -> confirmDialog.close());
                confirmDialog.getFooter().add(cancelBtn, confirmBtn);
                confirmDialog.open();
            });

            HorizontalLayout actions = new HorizontalLayout(edit, delete);
            return actions;
        }).setHeader("Azioni").setAutoWidth(true);
    }

    private void updateList() {
        
        
        String filter = searchField.getValue().toLowerCase();
        grid.setItems(buildingService.getBuildingsForUser(currentUser).stream()
            .filter(b -> b.getName().toLowerCase().contains(filter) || b.getAddress().toLowerCase().contains(filter))
            .toList());
    }

    private void openDialog(Building building) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Modifica Edificio");

        VerticalLayout form = new VerticalLayout();
        TextField nameField = new TextField("Nome");
        TextField addressField = new TextField("Indirizzo");
        ComboBox<University> uniSelect = new ComboBox<>("Università");

        nameField.setValue(building.getName() != null ? building.getName() : "");
        addressField.setValue(building.getAddress() != null ? building.getAddress() : "");
        nameField.setWidthFull(); addressField.setWidthFull(); uniSelect.setWidthFull();

        if (currentUser.getRole() == Role.ADMIN) {
            uniSelect.setItems(universityService.getAllUniversities());
            uniSelect.setItemLabelGenerator(University::getName);
            if (building.getUniversity() != null) uniSelect.setValue(building.getUniversity());
            form.add(nameField, addressField, uniSelect);
        } else {
            form.add(nameField, addressField);
        }

        Button save = new Button("Salva", e -> {
            if (nameField.getValue().isEmpty()) { Notification.show("Inserisci un nome!"); return; }
            building.setName(nameField.getValue());
            building.setAddress(addressField.getValue());
            if (currentUser.getRole() == Role.ADMIN) building.setUniversity(uniSelect.getValue());
            else building.setUniversity(currentUser.getUniversity());
            
            buildingService.saveBuilding(building);
            updateList();
            dialog.close();
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Annulla", e -> dialog.close());

        dialog.add(form);
        dialog.getFooter().add(cancel, save);
        dialog.open();
    }
}