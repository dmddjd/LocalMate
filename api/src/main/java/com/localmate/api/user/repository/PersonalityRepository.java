package com.localmate.api.user.repository;

import com.localmate.api.user.domain.Personality;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalityRepository extends JpaRepository<Personality, Long> {
}
