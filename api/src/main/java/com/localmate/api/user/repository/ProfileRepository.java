package com.localmate.api.user.repository;

import com.localmate.api.user.domain.Profile;
import com.localmate.api.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUser_UserId(Long userId);
}
