package com.localmate.api.chat.repository;

import com.localmate.api.chat.domain.ChatMsg;
import com.localmate.api.chat.domain.ChatMsgStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatMsgRepository extends JpaRepository<ChatMsg, Long> {
    void deleteAllByUser_UserIdIn(List<Long> targets);

    void deleteAllByChatRoom_ChatRoomIdIn(List<Long> targets);

    @Query("select distinct m from ChatMsg m " +
            "join fetch m.user u " +
            "join fetch u.profile p " +
            "left join fetch m.chatFiles cf " +
            "left join fetch cf.file " +
            "left join fetch m.replyToMsg r " +
            "left join fetch r.user re " +
            "where m.chatRoom.chatRoomId = :chatRoomId " +
            "and m.status = 'ACTIVE' " +
            "and not exists (select h from ChatMsgHidden h where h.chatMsg = m and h.user.userId = :userId) " +
            "order by m.sendTime asc")
    List<ChatMsg> findAllByChatRoomId(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);

    @Query("select count(m) from ChatMsg m " +
            "where m.chatRoom.chatRoomId = :chatRoomId " +
            "and (:lastReadMsgId is null or m.chatMsgId > :lastReadMsgId) " +
            "and m.status = 'ACTIVE'")
    int countUnread(@Param("chatRoomId") Long chatRoomId, @Param("lastReadMsgId") Long lastReadMsgId);

    @Query("select m from ChatMsg m " +
            "where m.status = 'DELETED' " +
            "and m.deletedDate < :cutoff")
    List<ChatMsg> findAllExpiredDeletedMsg(@Param("cutoff")LocalDateTime cutoff);

    Optional<ChatMsg> findFirstByChatRoom_ChatRoomIdAndStatusOrderByChatMsgIdDesc(Long chatRoomId, ChatMsgStatus status);
}
