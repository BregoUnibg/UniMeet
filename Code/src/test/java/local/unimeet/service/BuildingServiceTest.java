package local.unimeet.service;

import local.unimeet.entity.*;
import local.unimeet.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BuildingServiceTest {

    @Autowired private BuildingService buildingService;
    @MockBean private UserService userService; // Security safety net

    @Autowired private UniversityRepository uniRepo;
    @Autowired private BuildingRepository buildRepo;

    private University polytechnic;
    private University stateUni;

    @BeforeEach
    void setup() {
        polytechnic = uniRepo.save(new University("Polytechnic"));
        stateUni = uniRepo.save(new University("State University"));
    }

    @Test
    @DisplayName("Create building successfully")
    void testCreateBuilding() {
        Building b = buildingService.createBuilding("Science Block", "Polytechnic");

        assertNotNull(b.getId());
        assertEquals("Science Block", b.getName());
        assertEquals("Polytechnic", b.getUniversity().getName());
    }

    @Test
    @DisplayName("Fail to create duplicate building in same university")
    void testCreateDuplicate() {
        buildingService.createBuilding("Main Hall", "Polytechnic");

        assertThrows(IllegalArgumentException.class, () -> 
            buildingService.createBuilding("Main Hall", "Polytechnic")
        );
    }

    @Test
    @DisplayName("UniAdmin sees only their university's buildings")
    void testGetBuildingsForUser_UniAdmin() {
        // Setup data
        buildingService.createBuilding("Poly Build", "Polytechnic");
        buildingService.createBuilding("State Build", "State University");

        User uniAdmin = new User("rector", "pass", Role.UNI_ADMIN);
        UserProfile p = new UserProfile();
        p.setUniversity(polytechnic);
        p.setUser(uniAdmin);
        uniAdmin.setProfile(p);

        List<Building> result = buildingService.getBuildingsForUser(uniAdmin);

        assertEquals(1, result.size());
        assertEquals("Poly Build", result.get(0).getName());
    }

    @Test
    @DisplayName("Super Admin sees all buildings")
    void testGetBuildingsForUser_Admin() {
        long initialCount = buildRepo.count();

        buildingService.createBuilding("Poly Build", "Polytechnic");
        buildingService.createBuilding("State Build", "State University");

        User admin = new User("admin", "pass", Role.ADMIN);

        List<Building> result = buildingService.getBuildingsForUser(admin);

        assertEquals(initialCount + 2, result.size());
        
        assertTrue(result.stream().anyMatch(b -> b.getName().equals("Poly Build")));
        assertTrue(result.stream().anyMatch(b -> b.getName().equals("State Build")));
    }
}