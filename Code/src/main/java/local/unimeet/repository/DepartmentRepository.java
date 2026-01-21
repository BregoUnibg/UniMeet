package local.unimeet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import local.unimeet.entity.Department;
import local.unimeet.entity.University;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    // Fondamentale: trova i dipartimenti filtrati per università
    List<Department> findByUniversity(University university);
}
