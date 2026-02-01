package local.unimeet.repository.specifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import local.unimeet.dto.SessionSearchCriteria;
import local.unimeet.dto.UserSearchCriteria;
import local.unimeet.entity.Building;
import local.unimeet.entity.Room;
import local.unimeet.entity.SessionType;
import local.unimeet.entity.StudySession;
import local.unimeet.entity.StudyTable;
import local.unimeet.entity.Subject;
import local.unimeet.entity.University;
import local.unimeet.entity.UserProfile;

public class SearchSpecifications {

    //--- SESSION SPECIFICATIONS ---
    public static Specification<StudySession> searchSessions(SessionSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            //Show only public sessions
            predicates.add(cb.equal(root.get("type"), SessionType.PUBLIC));

            //Filter 1: Subject
            if (criteria.getSubject() != null) {
                predicates.add(cb.equal(root.get("subject"), criteria.getSubject()));
            }

            //Filter 2: Date (From... To...)
            if (criteria.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date").as(java.time.LocalDate.class), criteria.getFromDate()));
            }
            if (criteria.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date").as(java.time.LocalDate.class), criteria.getToDate()));
            }

            //Filter 3: Location
            //Navigate: Session -> Table -> Room -> Building -> University
            if (criteria.getUniversity() != null || criteria.getBuilding() != null || criteria.getRoom() != null) {
                
            	//Join<FatherEntity, SonEntity>
                Join<StudySession, StudyTable> tableJoin = root.join("studyTable");
                Join<StudyTable, Room> roomJoin = tableJoin.join("room");
                Join<Room, Building> buildingJoin = roomJoin.join("building");
                Join<Building, University> uniJoin = buildingJoin.join("university");
                
                //Filter universities
                if (criteria.getUniversity() != null) {
                    predicates.add(cb.equal(uniJoin, criteria.getUniversity()));
                    //Filter buildings
                    if (criteria.getBuilding() != null) {
                        predicates.add(cb.equal(buildingJoin, criteria.getBuilding()));
                        //Filter rooms
                        if (criteria.getRoom() != null) {
                            predicates.add(cb.equal(roomJoin, criteria.getRoom()));
                        }
                    }
                }
            }

            //Filter 4: Available seats
            if (criteria.isOnlyAvailable()) {
                // maxParticipants > participants.size()
                predicates.add(cb.greaterThan(
                    root.get("maxParticipants"), 
                    cb.size(root.get("participants"))
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    //--- USER SPECIFICATIONS ---
    public static Specification<UserProfile> searchUsers(UserSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            //Filter 1: Personal details (First Name, Last Name or Username)
            if (criteria.getFirstName() != null && !criteria.getFirstName().isEmpty()) {
                String pattern = "%" + criteria.getFirstName().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("firstName")), pattern));
            }

            if (criteria.getLastName() != null && !criteria.getLastName().isEmpty()) {
                String pattern = "%" + criteria.getLastName().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("lastName")), pattern));
            }

            if (criteria.getUsername() != null && !criteria.getUsername().isEmpty()) {
                String pattern = "%" + criteria.getUsername().toLowerCase() + "%";
                //Username is inside User's object connected to the UserProfile
                predicates.add(cb.like(cb.lower(root.get("user").get("username")), pattern));
            }

            //Filter 2: University
            if (criteria.getUniversity() != null) {
                predicates.add(cb.equal(root.get("university"), criteria.getUniversity()));
            }

            //Filter 3: Course name
            if (criteria.getCourse() != null) {
                predicates.add(cb.equal(root.get("courseName"), criteria.getCourse())); // verifica se il campo si chiama 'courseName'
            }

            //Filter 4: Minimum reputation
            if (criteria.getMinReputation() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("reputation"), criteria.getMinReputation()));
            }

            //Filter 5: Skills
            if (criteria.getSkill() != null) {
                Join<UserProfile, Subject> examsJoin = root.join("preferredCourses");
                predicates.add(cb.equal(examsJoin.get("id"), criteria.getSkill().getId()));
                
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
