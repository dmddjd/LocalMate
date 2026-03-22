package com.localmate.api.chat.dto;

import com.localmate.api.chat.domain.ChatMsgType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatMsgDto {
    private Long chatMsgId;
    private Long senderId;
    private String msgType;
    private String content;
    private String fileUrl;
    private LocalDateTime sendTime;
}
