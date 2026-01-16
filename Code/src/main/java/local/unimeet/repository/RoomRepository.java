package local.unimeet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import local.unimeet.entity.Building;
import local.unimeet.entity.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>{

	Optional<Room> findByNumberAndBuilding(int number, Building building);
	
	List<Room> findByBuildingId(long buildingId);
}


