package local.unimeet.repository;

import local.unimeet.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class StudyTableRepositoryTest {

    @Autowired
    private StudyTableRepository tableRepo;
    
    @Autowired
    private TestEntityManager entityManager;

    private Room room;
    private University uni;

    @BeforeEach
    void setupInfrastructure() {
        uni = new University("Unibg");
        entityManager.persist(uni);

        Building building = new Building("Building B1", "Address 1", uni);
        entityManager.persist(building);

        room = new Room(101, building);
        entityManager.persist(room);

        entityManager.persist(new StudyTable(1, 4, room));
        entityManager.persist(new StudyTable(2, 6, room));
    }

    @Test
    @DisplayName("Find table by specific number and room")
    void testFindByNumberAndRoom() {
        Optional<StudyTable> table = tableRepo.findByNumberAndRoom(1, room);
        
        assertTrue(table.isPresent());
        assertEquals(4, table.get().getCapacity());
        
        assertFalse(tableRepo.findByNumberAndRoom(99, room).isPresent());
    }

    @Test
    @DisplayName("Fetch tables with full details")
    void testFindAllDetailsByRoomId() {
    	
        entityManager.flush();
        entityManager.clear();

        List<StudyTable> tables = tableRepo.findAllDetailsByRoomId(room.getId());

        assertEquals(2, tables.size());
        
        StudyTable t = tables.get(0);
        assertNotNull(t.getRoom());
        assertNotNull(t.getRoom().getBuilding());
        assertNotNull(t.getRoom().getBuilding().getUniversity());
        assertEquals("Unibg", t.getRoom().getBuilding().getUniversity().getName());
    }

    @Test
    @DisplayName("Find tables by University")
    void testFindByRoom_Building_University() {
        
    	List<StudyTable> tables = tableRepo.findByRoom_Building_University(uni);
        assertEquals(2, tables.size());
        
        University otherUni = new University("Other Uni");
        entityManager.persist(otherUni);
        
        assertTrue(tableRepo.findByRoom_Building_University(otherUni).isEmpty());
    }
}