package com.localmate.api.chat.repository;

import com.localmate.api.chat.domain.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    void deleteAllByUser_UserIdIn(List<Long> userIds);
    void deleteAllByChatRoom_ChatRoomIdIn(List<Long> chatRoomIds);

    @Query("select count(cp) > 0 from ChatParticipant cp " +
            "where cp.chatRoom.chatRoomId = :chatRoomId " +
            "and cp.user.userId = :userId " +
            "and cp.status = 'JOIN'")
    boolean existActiveParticipant(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);

    @Query("select cp from ChatParticipant cp " +
            "where cp.chatRoom.chatRoomId = :chatRoomId " +
            "and cp.user.userId = :userId " +
            "and cp.status = 'JOIN'")
    Optional<ChatParticipant> findActiveParticipant(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);

    @Query("select count(cp) > 0 from ChatParticipant cp " +
            "where cp.chatRoom.chatRoomId = :chatRoomId " +
            "and cp.status = 'JOIN'")
    boolean existAnyActiveParticipant(@Param("chatRoomId") Long chatRoomId);

    @Query("select cp from ChatParticipant cp " +
            "join fetch cp.chatRoom cr " +
            "where cp.user.userId = :userId " +
            "and cp.status = 'JOIN' " +
            "and cr.status = 'ACTIVE' " +
            "order by cr.lastMsgDate desc")
    List<ChatParticipant> findMyParticipation(@Param("userId") Long userId);

    @Query("select cp from ChatParticipant cp " +
            "join fetch cp.user u " +
            "join fetch u.profile p " +
            "where cp.chatRoom.chatRoomId in :chatRoomIds " +
            "and cp.user.userId != :userId")
    List<ChatParticipant> findOpponents(@Param("chatRoomIds") List<Long> chatRoomIds, @Param("userId") Long userId);

    @Query("select cp.lastReadMsgId from ChatParticipant cp " +
            "where cp.chatRoom.chatRoomId = :chatRoomId " +
            "and cp.user.userId != :userId " +
            "and cp.status = 'JOIN'")
    Long findOpponentLastRead(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);

    @Query("select count(cp) from ChatParticipant cp " +
            "where cp.chatRoom.chatRoomId = :chatRoomId " +
            "and cp.status = 'JOIN'")
    int countActiveParticipants(@Param("chatRoomId") Long chatRoomId);
}
