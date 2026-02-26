package com.localmate.api.user.repository;

import com.localmate.api.user.domain.Profile;
import com.localmate.api.user.domain.ProfilePersonality;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfilePersonalityRepository extends JpaRepository<ProfilePersonality, Long> {
    void deleteAllByProfile(Profile profile);
}
