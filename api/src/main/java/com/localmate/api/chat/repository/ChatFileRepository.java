package com.localmate.api.chat.repository;

import com.localmate.api.chat.domain.ChatFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatFileRepository extends JpaRepository<ChatFile, Long> {
    void deleteAllByChatMsg_ChatMsgIdIn(List<Long> chatMsgIds);
    void deleteAllByChatMsg_ChatRoom_ChatRoomIdIn(List<Long> chatMsgIds);
    void deleteAllByChatMsg_User_UserIdIn(List<Long> userIds);
    Optional<ChatFile> findByFile_ChangeName(String changeName);

    @Query("select cf from ChatFile cf " +
            "join fetch cf.file " +
            "where cf.chatMsg.chatMsgId in :chatMsgIds")
    List<ChatFile> findAllByChatMsg_ChatMsgIdIn(List<Long> chatMsgIds);

    @Query("select cf from ChatFile cf " +
            "join fetch cf.file " +
            "where cf.chatMsg.chatRoom.chatRoomId in :chatRoomIds")
    List<ChatFile> findAllByChatMsg_ChatRoom_ChatRoomIdIn(List<Long> chatRoomIds);
}
