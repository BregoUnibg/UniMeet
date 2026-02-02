package local.unimeet.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import local.unimeet.entity.ColleagueRequest;
import local.unimeet.entity.User;

public interface ColleagueRequestRepository extends JpaRepository<ColleagueRequest, Long> {
    
    List<ColleagueRequest> findByReceiver(User receiver);

    //Check if a request already exists (both ways)
    @Query("SELECT count(c) > 0 FROM ColleagueRequest c WHERE " +
           "(c.sender = :u1 AND c.receiver = :u2) OR (c.sender = :u2 AND c.receiver = :u1)")
    boolean existsRequestBetween(User u1, User u2);
    
    @Query(value = "SELECT COUNT(*) > 0 FROM user_colleagues WHERE user_id = :u1 AND colleague_id = :u2", nativeQuery = true)
    boolean areTheyColleagues(@Param("u1") String username1, @Param("u2") String username2);
    
}