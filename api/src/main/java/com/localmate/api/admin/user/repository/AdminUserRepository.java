package com.localmate.api.admin.user.repository;

import com.localmate.api.user.domain.Gender;
import com.localmate.api.user.domain.Role;
import com.localmate.api.user.domain.User;
import com.localmate.api.user.domain.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AdminUserRepository extends JpaRepository<User, Long> {
    // 모든 유저 조회
    @Query("select u from User u " +
            "join fetch u.profile " +
            "where (:role is null or u.role = :role) " +
            "and (:gender is null or u.gender = :gender) " +
            "and (:status is null or u.status = :status)")
    List<User> getAllUser(@Param("role") Role role,
                          @Param("gender") Gender gender,
                          @Param("status") UserStatus status);

    // 유저 상세 조회
    @Query("select u from User u " +
            "join fetch u.profile " +
            "where u.userId = :userId")
    Optional<User> getUserDetail(@Param("userId") Long userId);
}
