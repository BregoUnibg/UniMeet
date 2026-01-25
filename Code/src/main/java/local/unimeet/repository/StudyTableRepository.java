package local.unimeet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import local.unimeet.entity.Room;
import local.unimeet.entity.StudyTable;
import local.unimeet.entity.University; // Importante: aggiunto import

public interface StudyTableRepository extends JpaRepository<StudyTable, Long> {
    
 
    
    Optional<StudyTable> findByNumberAndRoom(int number, Room room);
    
    List<StudyTable> findByRoomId(long roomId);
    
   
    @Query("SELECT s FROM StudyTable s " +
           "JOIN FETCH s.room r " +
           "JOIN FETCH r.building b " +
           "JOIN FETCH b.university u " +
           "WHERE r.id = :roomId")
    List<StudyTable> findAllDetailsByRoomId(@Param("roomId") Long roomId);

    // "Dammi tutti i tavoli che appartengono a questa Università"
    List<StudyTable> findByRoom_Building_University(University university);
}