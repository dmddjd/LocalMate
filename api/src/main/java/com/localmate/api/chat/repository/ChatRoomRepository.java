package com.localmate.api.chat.repository;

import com.localmate.api.chat.domain.ChatRoom;
import org.springframework.beans.PropertyValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("select distinct cp.chatRoom from ChatParticipant cp " +
            "where cp.user.userId in (:userId1, :userId2) " +
            "group by cp.chatRoom " +
            "having count(distinct cp.user.userId) = 2")
    Optional<ChatRoom> findExistRoom(@Param("userId1") Long userId,
                                     @Param("userId2") Long targetUserId);

    @Query("select cr from ChatRoom cr " +
            "where cr.status = 'DELETED' " +
            "and cr.deleteDate < :cutoff")
    List<ChatRoom> findAllDeletedChatRoom(@Param("cutoff") LocalDateTime cutoff);
}
