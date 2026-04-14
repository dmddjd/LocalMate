package com.localmate.api.chat.domain;

import com.localmate.api.global.file.domain.File;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ChatFile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatFileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_msg_id", nullable = false)
    private ChatMsg chatMsg;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    private File file;

    public ChatFile(ChatMsg chatMsg, File file) {
        this.chatMsg = chatMsg;
        this.file = file;
    }
}
