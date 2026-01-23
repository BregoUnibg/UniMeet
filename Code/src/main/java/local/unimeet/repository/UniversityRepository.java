package local.unimeet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import local.unimeet.entity.University;

@Repository
public interface UniversityRepository extends JpaRepository<University, String> {
    // Trova solo le università approvate (per non mostrare subito le proposte "Altro")
    List<University> findBy();
}
