package com.localmate.api.user.repository;

import com.localmate.api.user.domain.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface
RecommendationRepository extends JpaRepository<Recommendation, Long> {
    Optional<Recommendation> findByFromUser_UserIdAndToUser_UserId(Long FromUserId, Long toUserId);

    List<Recommendation> findAllByFromUser_UserIdIn(List<Long> userIds);
    void deleteAllByFromUser_UserIdIn(List<Long> userIds);
    void deleteAllByToUser_UserIdIn(List<Long> userIds);
}
