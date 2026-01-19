package local.unimeet.ui.sessionview;

import java.util.function.Consumer;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import local.unimeet.entity.Building;
import local.unimeet.entity.Room;
import local.unimeet.entity.StudyTable;
import local.unimeet.entity.University;
import local.unimeet.service.BuildingService;
import local.unimeet.service.RoomService;
import local.unimeet.service.StudyTableService;
import local.unimeet.service.UniversityService;

/**
 * This class creates a steb by step location selector for a study session
 */
public class placeWizzard extends Dialog {

	private final UniversityService universityService;
	private final BuildingService buildingService;
	private final RoomService roomService;
	private final StudyTableService studyTableService;
	
	private final Consumer<StudyTable> onComplete; // Callback when finished

    private University selectedUniversity;
    private Building selectedBuilding;
    private Room selectedRoom;
    private StudyTable selectedStudyTable;
    
    // UI Container that will be refilled each time
    private final VerticalLayout contentLayout = new VerticalLayout();

    public placeWizzard(UniversityService universityService, BuildingService buildingService, RoomService roomService, StudyTableService studyTableService, Consumer<StudyTable> onComplete) {
        
    	this.universityService = universityService;
    	this.buildingService = buildingService;
    	this.roomService = roomService;
    	this.studyTableService = studyTableService;
    	
    	this.onComplete = onComplete;
        
    	// Dialog Settings
        setModal(true);
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);
        setWidth("500px");
        setHeight("600px"); // Fixed height prevents jumping when content changes

        add(contentLayout);

        // Start at Step 1
        showUniversityStep();
    }

    // 1 - Select University
    private void showUniversityStep() {
        contentLayout.removeAll();

        H3 title = new H3("Step 1: Select University");
        
        // Using a Grid is better than a dropdown for a full-screen wizard
        Grid<University> grid = new Grid<>(University.class);
        grid.setColumns("name");
        grid.setItems(this.universityService.getAllUniversities());
        
        // Interaction: Clicking a row moves to next step
        grid.addItemClickListener(event -> {
            this.selectedUniversity = event.getItem();
            showBuildingStep();
        });

        contentLayout.add(title, grid);
    }

    // 2 - Select Building
    private void showBuildingStep() {
        contentLayout.removeAll();

        H3 title = new H3("Step 2: Select Building");
        H4 subtitle = new H4("at " + selectedUniversity.getName()); // Context for user

        Grid<Building> grid = new Grid<>(Building.class);
        grid.setColumns("name", "address");
        grid.setItems(this.buildingService.getBuildingsByUniversity(this.selectedUniversity.getName()));

        grid.addItemClickListener(event -> {
            this.selectedBuilding = event.getItem();
            showRoomStep();
        });

        // Navigation: Back Button
        Button backButton = new Button("Back", VaadinIcon.ARROW_LEFT.create(), e -> showUniversityStep());

        contentLayout.add(title, subtitle, grid, backButton);
    }
    
    // 3 - Select Room
    private void showRoomStep() {
        contentLayout.removeAll();

        H3 title = new H3("Step 3: Select Room");
        H4 subtitle = new H4("at " + selectedBuilding.getName()); // Context for user

        Grid<Room> grid = new Grid<>(Room.class);
        grid.setColumns("number");
        grid.setItems(this.roomService.getRoomByBuilding(this.selectedBuilding.getId()));

        grid.addItemClickListener(event -> {
            this.selectedRoom = event.getItem();
            showStudyTableStep();
        });

        // Navigation: Back Button
        Button backButton = new Button("Back", VaadinIcon.ARROW_LEFT.create(), e -> showBuildingStep());

        contentLayout.add(title, subtitle, grid, backButton);
    }

    // 4 - Select StudyTable
    private void showStudyTableStep() {
        contentLayout.removeAll();

        H3 title = new H3("Step 4: Select Table");
        H4 subtitle = new H4("in " + selectedRoom.getNumber());

        Grid<StudyTable> grid = new Grid<>(StudyTable.class);
        grid.setColumns("number", "capacity");
        grid.setItems(studyTableService.getStudyTableByRoom(selectedRoom.getId()));

        grid.addItemClickListener(event -> {
            this.selectedStudyTable = event.getItem();
            
            // WIZARD IS COMPLETED: Run the callback and close
            onComplete.accept(this.selectedStudyTable);
            this.close();
        });

        // Navigation: Back Button
        Button backButton = new Button("Back", VaadinIcon.ARROW_LEFT.create(), e -> showRoomStep());

        contentLayout.add(title, subtitle, grid, backButton);
    }
}