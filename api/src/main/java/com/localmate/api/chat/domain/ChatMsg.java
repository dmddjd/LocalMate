package com.localmate.api.chat.domain;

import com.localmate.api.global.file.domain.File;
import com.localmate.api.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ChatMsg {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatMsgId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChatMsgType msgType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private File file;

    private String content;

    @Column(nullable = false)
    private LocalDateTime sendTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChatMsgStatus status;

    public ChatMsg(ChatRoom chatRoom, User user, ChatMsgType msgType, File file, String content) {
        this.chatRoom = chatRoom;
        this.user = user;
        this.msgType = msgType;
        this.file = file;
        this.content = content;
        this.status = ChatMsgStatus.ACTIVE;
    }

    @PrePersist
    public void prePersist() {
        this.sendTime = LocalDateTime.now();
    }
}
