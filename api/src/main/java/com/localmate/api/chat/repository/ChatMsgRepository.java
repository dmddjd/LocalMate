package com.localmate.api.chat.repository;

import com.localmate.api.chat.domain.ChatMsg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMsgRepository extends JpaRepository<ChatMsg, Long> {
    void deleteAllByUser_UserIdIn(List<Long> targets);

    void deleteAllByChatRoom_ChatRoomIdIn(List<Long> targets);

    @Query("select m from ChatMsg m " +
            "join fetch m.user u " +
            "join fetch u.profile p " +
            "where m.chatRoom.chatRoomId = :chatRoomId " +
            "and m.status = 'ACTIVE' " +
            "order by m.sendTime asc")
    List<ChatMsg> findAllByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    @Query("select count(m) from ChatMsg m " +
            "where m.chatRoom.chatRoomId = :chatRoomId " +
            "and (:lastReadMsgId is null or m.chatMsgId > :lastReadMsgId) " +
            "and m.status = 'ACTIVE'")
    int countUnread(@Param("chatRoomId") Long chatRoomId, @Param("lastReadMsgId") Long lastReadMsgId);
}
