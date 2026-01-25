package local.unimeet.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import local.unimeet.entity.Building;
import local.unimeet.entity.Role;
import local.unimeet.entity.Room;
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

    // Creazione standard
    @Transactional
    public Room createRoom(int roomNumber, long buildingId) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new EntityNotFoundException("Building not found: " + buildingId));
        
        if (roomRepository.findByNumberAndBuilding(roomNumber, building).isPresent()) {
            throw new IllegalArgumentException("Room " + roomNumber + " already exists in building " + buildingId);
        }
        
        Room newRoom = new Room();
        newRoom.setNumber(roomNumber);
        newRoom.setBuilding(building);
        
        return roomRepository.save(newRoom);
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

    
    public List<Room> getRoomByBuilding(Long buildingId) {
       
        return roomRepository.findByBuilding_Id(buildingId);
    }
}