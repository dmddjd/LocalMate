package com.localmate.api.chat.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ChatMsgHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_msg_id", nullable = false)
    private ChatMsg chatMsg;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime editedDate;

    public ChatMsgHistory(ChatMsg chatMsg, String content) {
        this.chatMsg = chatMsg;
        this.content = content;
    }

    @PrePersist
    public void prePersist() {
        this.editedDate = LocalDateTime.now();
    }
}
