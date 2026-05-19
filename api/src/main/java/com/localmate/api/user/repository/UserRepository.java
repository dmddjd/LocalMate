package com.localmate.api.user.repository;

import com.localmate.api.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(String id);

    Optional<User> findByUserId(Long userId);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByNickname(String nickname);

    Optional<User> findByUserNameAndEmail(String userName, String email);

    Optional<User> findByIdAndEmail(String id, String email);

    @Query("select u from User u " +
            "where u.status = 'DELETED' " +
            "and u.withdrawDate < :cutoff")
    List<User> findAllDeletedUser(@Param("cutoff") LocalDateTime cutoff);
}
