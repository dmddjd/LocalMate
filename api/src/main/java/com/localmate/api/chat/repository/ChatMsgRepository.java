package com.localmate.api.chat.repository;

import com.localmate.api.chat.domain.ChatMsg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMsgRepository extends JpaRepository<ChatMsg, Long> {
    void deleteAllByUser_UserIdIn(List<Long> targets);
}
