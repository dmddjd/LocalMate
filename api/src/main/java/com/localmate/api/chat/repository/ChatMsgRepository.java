package com.localmate.api.chat.repository;

import com.localmate.api.chat.domain.ChatMsg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMsgRepository extends JpaRepository<ChatMsg, Long> {
}
