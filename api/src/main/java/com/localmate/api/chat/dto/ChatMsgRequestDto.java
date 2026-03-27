package com.localmate.api.chat.dto;

import com.localmate.api.chat.domain.ChatMsgType;
import lombok.Getter;

@Getter
public class ChatMsgRequestDto {
    private String content;
    private ChatMsgType msgType;
}
