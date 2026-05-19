package com.localmate.api.user.repository;

import com.localmate.api.user.domain.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface
RecommendationRepository extends JpaRepository<Recommendation, Long> {
    Optional<Recommendation> findByFromUser_UserIdAndToUser_UserId(Long FromUserId, Long toUserId);

    @Query("select r from Recommendation r " +
            "join fetch r.toUser u " +
            "join fetch u.profile " +
            "where r.fromUser.userId in :userIds")
    List<Recommendation> findAllByFromUser_UserIdIn(@Param("userIds") List<Long> userIds);

    void deleteAllByFromUser_UserIdIn(List<Long> userIds);
    void deleteAllByToUser_UserIdIn(List<Long> userIds);
}
