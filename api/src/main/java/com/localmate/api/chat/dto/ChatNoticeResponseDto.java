package com.localmate.api.chat.dto;

import com.localmate.api.chat.domain.ChatMsg;
import com.localmate.api.chat.domain.ChatMsgType;
import lombok.Getter;

@Getter
public class ChatNoticeResponseDto {
    private Long chatMsgId;
    private String content;

    public ChatNoticeResponseDto(ChatMsg chatMsg) {
        this.chatMsgId = chatMsg.getChatMsgId();
        if (chatMsg.getMsgType() == ChatMsgType.TEXT) {
            this.content = chatMsg.getContent();
        } else if (chatMsg.getMsgType() == ChatMsgType.IMAGE) {
            this.content = "사진이 공지되었습니다.";
        } else {
            this.content = "동영상이 공지되었습니다.";
        }
    }
}
