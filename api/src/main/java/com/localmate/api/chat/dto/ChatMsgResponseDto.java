package com.localmate.api.chat.dto;

import com.localmate.api.chat.domain.ChatMsg;
import com.localmate.api.chat.domain.ChatMsgType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatMsgResponseDto {
    private Long chatMsgId;
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private String content;
    private ChatMsgType msgType;
    private LocalDateTime sendTime;
    private int unreadCount;

    public ChatMsgResponseDto(ChatMsg chatMsg) {
        this.chatMsgId = chatMsg.getChatMsgId();
        this.userId = chatMsg.getUser().getUserId();
        this.nickname = chatMsg.getUser().getNickname();
        this.profileImageUrl = chatMsg.getUser().getProfile().getProfileImagePath();
        this.content = chatMsg.getContent();
        this.msgType = chatMsg.getMsgType();
        this.sendTime = chatMsg.getSendTime();
        this.unreadCount = 0;
    }

    public ChatMsgResponseDto(ChatMsg chatMsg, int unreadCount) {
        this.chatMsgId = chatMsg.getChatMsgId();
        this.userId = chatMsg.getUser().getUserId();
        this.nickname = chatMsg.getUser().getNickname();
        this.profileImageUrl = chatMsg.getUser().getProfile().getProfileImagePath();
        this.content = chatMsg.getContent();
        this.msgType = chatMsg.getMsgType();
        this.sendTime = chatMsg.getSendTime();
        this.unreadCount = unreadCount;

    }
}
