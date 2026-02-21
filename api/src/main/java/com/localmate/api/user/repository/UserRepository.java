package com.localmate.api.user.repository;

import com.localmate.api.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(String id);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByUserNameAndEmail(String userName, String email);

    Optional<User> findByIdAndEmail(String id, String email);
}
