package local.unimeet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import local.unimeet.entity.ParticipantReview;
import local.unimeet.entity.StudySession;
import local.unimeet.entity.User;

@Repository
public interface ParticipantReviewRepository extends JpaRepository<ParticipantReview, Long> {

    boolean existsByReviewerAndTargetAndSession(User reviewer, User target, StudySession session);

}