package com.localmate.api.chat.domain;

import com.localmate.api.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ChatMsgHidden {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hiddenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_msg_id", nullable = false)
    private ChatMsg chatMsg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name =  "user_id", nullable = false)
    private User user;

    public ChatMsgHidden(ChatMsg chatMsg, User user) {
        this.chatMsg = chatMsg;
        this.user = user;
    }
}
