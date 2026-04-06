package com.localmate.api.chat.repository;

import com.localmate.api.chat.domain.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    void deleteAllByUser_UserIdIn(List<Long> targets);
}
