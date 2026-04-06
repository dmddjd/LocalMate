package com.localmate.api.chat.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ChatRoom {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    @Column(nullable = false)
    private LocalDateTime createDate;

    private String lastMsgContent;

    private LocalDateTime lastMsgDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChatRoomStatus status = ChatRoomStatus.ACTIVE;

    @PrePersist
    public void prePersist() {
        this.createDate = LocalDateTime.now();
    }

    public void updateLastMsg(String lastMsgContent) {
        this.lastMsgContent = lastMsgContent;
        this.lastMsgDate = LocalDateTime.now();
    }
}
