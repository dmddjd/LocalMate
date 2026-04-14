package com.localmate.api.chat.repository;

import com.localmate.api.chat.domain.ChatFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatFileRepository extends JpaRepository<ChatFile, Long> {
    void deleteAllByChatMsg_ChatRoom_ChatRoomIdIn(List<Long> chatMsgIds);
    void deleteAllByChatMsg_User_UserIdIn(List<Long> userIds);
}
