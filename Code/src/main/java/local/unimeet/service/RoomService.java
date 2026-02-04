package local.unimeet.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import local.unimeet.entity.Building;
import local.unimeet.entity.Role;
import local.unimeet.entity.Room;
import local.unimeet.entity.University;
import local.unimeet.entity.User;
import local.unimeet.repository.BuildingRepository;
import local.unimeet.repository.RoomRepository;

@Service
public class RoomService {

	private final RoomRepository roomRepository;
	private final BuildingRepository buildingRepository;

	
	public RoomService(RoomRepository roomRepository, BuildingRepository buildingRepository) {
		this.roomRepository = roomRepository;
		this.buildingRepository = buildingRepository;
		
	}
	
	@Transactional
	public Room createRoom(int roomNumber, long buildingId){
		
		Building building = buildingRepository.findById(buildingId)
				.orElseThrow(() -> new EntityNotFoundException("Building not found: " + buildingId));
		
		if (roomRepository.findByNumberAndBuilding(roomNumber, building).isPresent()) {
            throw new IllegalArgumentException("Room'" + roomNumber+ "' already exists in " + buildingId);
        }
		
		
		Room newRoom = new Room();
		newRoom.setNumber(roomNumber);
		newRoom.setBuilding(building);
		
		return roomRepository.save(newRoom);
	}
	
	public Room getRoomByNumberAndBuildingId(int number, long buildingId) {
		
		Building building = buildingRepository.findById(buildingId)
				.orElseThrow(() -> new EntityNotFoundException("Building not found: " + buildingId));

	    Room room=  roomRepository.findByNumberAndBuilding(number, building)
	    			.orElseThrow(() -> new EntityNotFoundException("Room not found: " + number));
	    
	    
	    return room;
		
	}
	
	public Room getRoomById(long id) {
		
		Room room = roomRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Room not found: " + id));
		
		return room;
	}
	
	 public List<Room> getRoomByBuilding(Long buildingId) {
	        
	        if (!buildingRepository.existsById(buildingId)) {
	            throw new EntityNotFoundException("Building not found with Id : " + buildingId);
	        }

	        return roomRepository.findByBuildingId(buildingId);
	    }
	 
	 
	 public List<Room> getRoomsForUser(User user) {
	        if (user.getRole() == Role.ADMIN) {
	            return roomRepository.findAll();
	        } else if (user.getRole() == Role.UNI_ADMIN) {
	            return roomRepository.findByBuilding_University(user.getUniversity());
	        }
	        return List.of();
	    }
	 
	 
	  public void saveRoom(Room room) {
	        if (room == null) return;
	        roomRepository.save(room);
	    }

	    public void deleteRoom(Room room) {
	        roomRepository.delete(room);
	    }
	    
	    /*@Transactional
	    public void deleteRoom(Room room) {
	        Building managedBuilding = buildingRepository.findById(buildingFromUi.getId())
	                .orElseThrow(() -> new RuntimeException("Edificio non trovato nel DB"));
	        
	        University parentUniversity = managedBuilding.getUniversity();

	        if (parentUniversity != null) {
	            parentUniversity.removeBuilding(managedBuilding);
	            
	            managedBuilding.setUniversity(null);
	            
	            RoomRepository.save(parentUniversity);
	        }
	        
	        roomRepository.delete(managedBuilding);
	    }*/
	
}
