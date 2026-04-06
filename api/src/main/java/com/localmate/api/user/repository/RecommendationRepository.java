package com.localmate.api.user.repository;

import com.localmate.api.user.domain.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    void deleteAllByUser_UserIdIn(List<Long> targets);
}
