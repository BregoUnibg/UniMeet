package local.unimeet.service;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import local.unimeet.entity.Room;
import local.unimeet.entity.StudyTable;
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
	
}
