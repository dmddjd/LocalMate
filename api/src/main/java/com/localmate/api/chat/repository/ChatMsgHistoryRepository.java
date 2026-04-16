package com.localmate.api.chat.repository;

import com.localmate.api.chat.domain.ChatMsgHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMsgHistoryRepository extends JpaRepository<ChatMsgHistory, Long> {
    void deleteAllByChatMsg_ChatMsgIdIn(List<Long> chatMsgIds);
    void deleteAllByChatMsg_ChatRoom_ChatRoomIdIn(List<Long> chatRoomIds);
    void deleteAllByChatMsg_User_UserIdIn(List<Long> userIds);
}
