package local.unimeet.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UniversityTest {

    @Test
    @DisplayName("University and its buildings see eachother")
    void testAddBuilding() {
        
    	University uni = new University("UniMeet University");
        Building building = new Building();
        building.setName("Edificio A");

        uni.addBuilding(building);

        // Assert
        assertTrue(uni.getBuildings().contains(building));
        assertEquals(uni, building.getUniversity());
    }

    @Test
    @DisplayName("Once removed university and building no longer see eachother")
    void testRemoveBuilding() {
        
    	University uni = new University("UniMeet University");
        Building building = new Building();
        uni.addBuilding(building);

        uni.removeBuilding(building);

        assertFalse(uni.getBuildings().contains(building));
        assertNull(building.getUniversity());
    }
}