package local.unimeet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import local.unimeet.entity.Building;
import local.unimeet.entity.Room;
import local.unimeet.entity.StudyTable;

public interface StudyTableRepository extends JpaRepository<StudyTable, Long>{
	
	Optional<StudyTable> findByNumberAndRoom(int number, Room room);
	
	List<StudyTable> findByRoomId(long roomId);
	
}
