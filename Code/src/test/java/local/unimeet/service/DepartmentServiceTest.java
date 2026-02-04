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
class DepartmentServiceTest {

    @Autowired private DepartmentService deptService;
    @MockBean private UserService userService;

    @Autowired private UniversityRepository uniRepo;
    @Autowired private DepartmentRepository deptRepo;

    private University uni;

    @BeforeEach
    void setup() {
        uni = new University("Tech University");
        uniRepo.save(uni);
    }

    @Test
    @DisplayName("Create department successfully")
    void testCreateDepartment_Success() {
        Department dept = deptService.createDepartment("Computer Science", uni);

        assertNotNull(dept.getId());
        assertEquals("Computer Science", dept.getName());
        assertEquals("Tech University", dept.getUniversity().getName());
    }

    @Test
    @DisplayName("Fail to create duplicate department in same university")
    void testCreateDepartment_Duplicate() {
        deptService.createDepartment("Math", uni);

        assertThrows(IllegalArgumentException.class, () -> 
            deptService.createDepartment("Math", uni)
        );
    }

    @Test
    @DisplayName("Get departments by university")
    void testGetDepartmentsByUniversity() {
        deptService.createDepartment("Physics", uni);
        deptService.createDepartment("Chemistry", uni);

        List<Department> list = deptService.getDepartmentsByUniversity(uni);
        assertEquals(2, list.size());
    }
}