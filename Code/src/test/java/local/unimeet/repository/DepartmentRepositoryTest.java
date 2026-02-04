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
class DepartmentRepositoryTest {

    @Autowired
    private DepartmentRepository deptRepo;

    @Autowired
    private TestEntityManager entityManager;

    private University uni1;
    private University uni2;

    @BeforeEach
    void setupUniversity() {
        uni1 = new University("Unibg");
        entityManager.persist(uni1);

        uni2 = new University("Unimi");
        entityManager.persist(uni2);

        entityManager.persist(new Department("Engineering", uni1));
        entityManager.persist(new Department("Architecture", uni1));
        entityManager.persist(new Department("Medicine", uni2));
    }

    @Test
    @DisplayName("Find departments by university")
    void testFindByUniversity() {
        List<Department> polyDepts = deptRepo.findByUniversity(uni1);
        
        assertEquals(2, polyDepts.size());
        assertTrue(polyDepts.stream().anyMatch(d -> d.getName().equals("Engineering")));
    }

    @Test
    @DisplayName("Find department by name and university")
    void testFindByNameAndUniversity() {
        Optional<Department> dept = deptRepo.findByNameAndUniversity("Medicine", uni2);
        
        assertTrue(dept.isPresent());
        assertEquals("Unimi", dept.get().getUniversity().getName());
        
        // Wrong combination check
        Optional<Department> wrongUni = deptRepo.findByNameAndUniversity("Medicine", uni1);
        assertFalse(wrongUni.isPresent());
    }
}