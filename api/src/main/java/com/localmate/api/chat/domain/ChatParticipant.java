package com.localmate.api.chat.domain;

import com.localmate.api.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ChatParticipant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatParticipantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Long lastReadMsgId;

    private LocalDateTime lastReadDate;

    private LocalDateTime leaveDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChatRoomParticipantStatus status;

    public ChatParticipant(ChatRoom chatRoom, User user) {
        this.chatRoom = chatRoom;
        this.user = user;
        this.status = ChatRoomParticipantStatus.JOIN;
    }

    public void updateLastRead(Long chatMsgId) {
        this.lastReadMsgId = chatMsgId;
        this.lastReadDate = LocalDateTime.now();
    }

    public void leave() {
        this.status = ChatRoomParticipantStatus.LEAVE;
        this.leaveDate = LocalDateTime.now();
    }
}
