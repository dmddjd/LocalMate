package com.localmate.api.user.repository;

import com.localmate.api.user.domain.Gender;
import com.localmate.api.user.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    // 프로필 조회
    @Query("select p from Profile p " +
            "join fetch p.user u " +
            "left join fetch p.profilePersonalities pp " +
            "left join fetch pp.personality " +
            "where p.user.userId = :userId " +
            "and u.status = 'ACTIVE'")
    Optional<Profile> findByUser_UserId(@Param("userId") Long userId);

    // 현지인 목록 조회
    @Query("select p from Profile p " +
            "join fetch p.user u "  +
            "left join fetch p.profilePersonalities pp " +
            "left join fetch pp.personality " +
            "where p.localMode = true " +
            "and u.country = :country " +
            "and u.city = :city " +
            "and (:gender is null or u.gender = :gender)" +
            "and u.status = 'ACTIVE'")
    List<Profile> findUsers(@Param("country") String country,
                            @Param("city") String city,
                            @Param("gender") Gender gender);

}
