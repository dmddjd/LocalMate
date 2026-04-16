package com.localmate.api.chat.domain;

import com.localmate.api.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "chatMsg")
    private List<ChatFile> chatFiles = new ArrayList<>();

    private String content;

    @Column(nullable = false)
    private boolean edited = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_msg_id")
    private ChatMsg replyToMsg;

    @Column(nullable = false)
    private LocalDateTime sendTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChatMsgStatus status;

    private LocalDateTime deletedDate;

    // 텍스트용
    public ChatMsg(ChatRoom chatRoom, User user, ChatMsgType msgType, String content, ChatMsg replyToMsg) {
        this.chatRoom = chatRoom;
        this.user = user;
        this.msgType = msgType;
        this.content = content;
        this.replyToMsg = replyToMsg;
        this.status = ChatMsgStatus.ACTIVE;
    }

    // 파일용
    public ChatMsg(ChatRoom chatRoom, User user, ChatMsgType msgType, ChatMsg replyToMsg) {
        this.chatRoom = chatRoom;
        this.user = user;
        this.msgType = msgType;
        this.replyToMsg = replyToMsg;
        this.status = ChatMsgStatus.ACTIVE;
    }

    public void edit(String newContent) {
        this.content = newContent;
        this.edited = true;
    }

    public void delete() {
        this.status = ChatMsgStatus.DELETED;
        this.deletedDate = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        this.sendTime = LocalDateTime.now();
    }
}
