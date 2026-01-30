package local.unimeet.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import local.unimeet.entity.Role;
import local.unimeet.entity.Room;
import local.unimeet.entity.StudyTable;
import local.unimeet.entity.User;
import local.unimeet.repository.RoomRepository;
import local.unimeet.repository.StudyTableRepository;

@Service
public class StudyTableService {
	
	private final StudyTableRepository studyTableRepository;
	private final RoomRepository roomRepository;
	
	public StudyTableService(StudyTableRepository studyTableRepository, RoomRepository roomRepository) {
		this.studyTableRepository = studyTableRepository;
		this.roomRepository = roomRepository;
	}
	
	@Transactional
	public StudyTable createStudyTable(int studyTableNumber, long roomId){
		
		Room room = roomRepository.findById(roomId)
				.orElseThrow(() -> new EntityNotFoundException("Room not found: " + roomId));
		
		 if (studyTableRepository.findByNumberAndRoom(studyTableNumber, room).isPresent()) {
	            throw new IllegalArgumentException("Building '" + studyTableNumber + "' already exists in " + roomId);
	        }
		
		 StudyTable newStudyTable = new StudyTable();
		 newStudyTable.setNumber(studyTableNumber);
		 newStudyTable.setRoom(room);
		 
		 return studyTableRepository.save(newStudyTable);
		 
	}
	
	public StudyTable getStudyTableBynumberAndRoomId(int number, long roomId) {
		
		Room room = roomRepository.findById(roomId)
				.orElseThrow(() -> new EntityNotFoundException("Room not found: " + roomId));
		
		StudyTable studyTable = studyTableRepository.findByNumberAndRoom(number, room)
				.orElseThrow(() -> new EntityNotFoundException("Study Table not found: " + number));
		
		return studyTable;
		
	}
	
	public StudyTable getStudyTableById(long id) {
		
		StudyTable studyTable = studyTableRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Study Table not found: " + id));
		
		return studyTable;
		
	}
	
	public List<StudyTable> getStudyTableByRoom(Long roomId) {
        
        if (!roomRepository.existsById(roomId)) {
            throw new EntityNotFoundException("Room not found with Id : " + roomId);
        }

        return studyTableRepository.findAllDetailsByRoomId(roomId);
    }
	
	 public List<StudyTable> getTablesForUser(User user) {
	        if (user.getRole() == Role.ADMIN) {
	            // SuperAdmin vede tutto
	            return studyTableRepository.findAll();
	        } else if (user.getRole() == Role.UNI_ADMIN) {
	            // Rettore vede solo tavoli della sua università
	           
	            return studyTableRepository.findByRoom_Building_University(user.getUniversity());
	        }
	        return List.of();
	    }

	    
	    public void saveTable(StudyTable table) {
	        if (table == null) return;
	       
	        studyTableRepository.save(table);
	    }

	   
	    public void deleteTable(StudyTable table) {
	        studyTableRepository.delete(table);
	    }
	    
	
}
