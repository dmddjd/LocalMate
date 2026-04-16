package com.localmate.api.chat.repository;

import com.localmate.api.chat.domain.ChatFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatFileRepository extends JpaRepository<ChatFile, Long> {
    void deleteAllByChatMsg_ChatMsgIdIn(List<Long> chatMsgIds);
    void deleteAllByChatMsg_ChatRoom_ChatRoomIdIn(List<Long> chatMsgIds);
    void deleteAllByChatMsg_User_UserIdIn(List<Long> userIds);
    Optional<ChatFile> findByFile_ChangeName(String changeName);

    List<ChatFile> findAllByChatMsg_ChatMsgIdIn(List<Long> chatMsgIds);
    List<ChatFile> findAllByChatMsg_ChatRoom_ChatRoomIdIn(List<Long> chatRoomIds);
    List<ChatFile> findAllByChatMsg_User_UserIdIn(List<Long> userIds);
}
