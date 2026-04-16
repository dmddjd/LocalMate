package com.localmate.api.chat.repository;

import com.localmate.api.chat.domain.ChatMsgHidden;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMsgHiddenRepository extends JpaRepository<ChatMsgHidden, Long> {
    void deleteAllByChatMsg_ChatMsgIdIn(List<Long> chatMsgIds);
    void deleteAllByChatMsg_ChatRoom_ChatRoomIdIn(List<Long> chatRoomIds);
    void deleteAllByChatMsg_User_UserIdIn(List<Long> targets);
    void deleteAllByUser_UserIdIn(List<Long> userIds);
    long countByChatMsg_ChatMsgId(Long chatMsgId);
}
