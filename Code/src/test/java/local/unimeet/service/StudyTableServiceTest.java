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
class StudyTableServiceTest {

    @Autowired private StudyTableService tableService;
    @MockBean private UserService userService;

    @Autowired private UniversityRepository uniRepo;
    @Autowired private BuildingRepository buildRepo;
    @Autowired private RoomRepository roomRepo;
    @Autowired private StudyTableRepository tableRepo;

    private Room polyRoom;
    private Room stateRoom;
    private University polytechnic;
    private University stateUni;

    @BeforeEach
    void setup() {
        polytechnic = uniRepo.save(new University("Polytechnic"));
        Building b1 = buildRepo.save(new Building("B1", "Add", polytechnic));
        polyRoom = roomRepo.save(new Room(101, b1));

        stateUni = uniRepo.save(new University("State Uni"));
        Building b2 = buildRepo.save(new Building("B2", "Add", stateUni));
        stateRoom = roomRepo.save(new Room(202, b2));
    }

    @Test
    @DisplayName("Create table successfully")
    void testCreateStudyTable() {
        StudyTable t = tableService.createStudyTable(1, polyRoom.getId());

        assertNotNull(t.getId());
        assertEquals(1, t.getNumber());
        assertEquals(101, t.getRoom().getNumber());
    }

    @Test
    @DisplayName("Fail to create duplicate table number in same room")
    void testCreateDuplicate() {
        tableService.createStudyTable(5, polyRoom.getId());

        assertThrows(IllegalArgumentException.class, () -> 
            tableService.createStudyTable(5, polyRoom.getId())
        );
    }

    @Test
    @DisplayName("UniAdmin sees only tables in their university")
    void testGetTablesForUser_UniAdmin() {
        tableService.createStudyTable(1, polyRoom.getId());
        tableService.createStudyTable(1, stateRoom.getId());

        User uniAdmin = new User("rector", "pass", Role.UNI_ADMIN);
        UserProfile p = new UserProfile();
        p.setUniversity(polytechnic); 
        p.setUser(uniAdmin);
        uniAdmin.setProfile(p);

        List<StudyTable> result = tableService.getTablesForUser(uniAdmin);

        assertEquals(1, result.size());
        assertEquals("Polytechnic", result.get(0).getRoom().getBuilding().getUniversity().getName());
    }

    @Test
    @DisplayName("Get tables by room ID (Details Fetch check)")
    void testGetStudyTableByRoom() {
        tableService.createStudyTable(10, polyRoom.getId());
        tableService.createStudyTable(11, polyRoom.getId());

        List<StudyTable> tables = tableService.getStudyTableByRoom(polyRoom.getId());

        assertEquals(2, tables.size());
        assertNotNull(tables.get(0).getRoom().getBuilding()); 
    }
}