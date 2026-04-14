package com.localmate.api.chat.dto;

import com.localmate.api.chat.domain.ChatFile;
import com.localmate.api.chat.domain.ChatMsg;
import com.localmate.api.chat.domain.ChatMsgType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ChatMsgResponseDto {
    private Long chatMsgId;
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private String content;
    private boolean edited;
    private Long replyToMsgId;
    private String replyToNickname;
    private String replyToContent;
    private List<String> fileUrls;
    private ChatMsgType msgType;
    private LocalDateTime sendTime;
    private int unreadCount;

    // getMsg용
    public ChatMsgResponseDto(ChatMsg chatMsg) {
        this.chatMsgId = chatMsg.getChatMsgId();
        this.userId = chatMsg.getUser().getUserId();
        this.nickname = chatMsg.getUser().getNickname();
        this.profileImageUrl = chatMsg.getUser().getProfile().getProfileImagePath();
        this.content = chatMsg.getContent();
        this.edited = chatMsg.isEdited();
        this.fileUrls = chatMsg.getChatFiles().stream()
                .map(cf -> cf.getFile().getPath())
                .toList();
        this.msgType = chatMsg.getMsgType();
        this.sendTime = chatMsg.getSendTime();
        this.unreadCount = 0;

        if (chatMsg.getReplyToMsg() != null) {
            ChatMsg reply = chatMsg.getReplyToMsg();
            this.replyToMsgId = reply.getChatMsgId();
            this.replyToNickname = reply.getUser().getNickname();
            this.replyToContent = reply.getContent() != null ? reply.getContent() :
                    reply.getMsgType() == ChatMsgType.IMAGE ? "사진" : "동영상";
        }
    }

    // sendMsg용
    public ChatMsgResponseDto(ChatMsg chatMsg, int unreadCount) {
        this.chatMsgId = chatMsg.getChatMsgId();
        this.userId = chatMsg.getUser().getUserId();
        this.nickname = chatMsg.getUser().getNickname();
        this.profileImageUrl = chatMsg.getUser().getProfile().getProfileImagePath();
        this.content = chatMsg.getContent();
        this.edited = chatMsg.isEdited();
        this.fileUrls = chatMsg.getChatFiles().stream()
                .map(cf -> cf.getFile().getPath())
                .toList();
        this.msgType = chatMsg.getMsgType();
        this.sendTime = chatMsg.getSendTime();
        this.unreadCount = unreadCount;

        if (chatMsg.getReplyToMsg() != null) {
            ChatMsg reply = chatMsg.getReplyToMsg();
            this.replyToMsgId = reply.getChatMsgId();
            this.replyToNickname = reply.getUser().getNickname();
            this.replyToContent = reply.getContent() != null ? reply.getContent() :
                    reply.getMsgType() == ChatMsgType.IMAGE ? "사진" : "동영상";
        }
    }

    // sendFileMsg용
    public ChatMsgResponseDto(ChatMsg chatMsg, int unreadCount, List<ChatFile> chatFiles) {
        this.chatMsgId = chatMsg.getChatMsgId();
        this.userId = chatMsg.getUser().getUserId();
        this.nickname = chatMsg.getUser().getNickname();
        this.profileImageUrl = chatMsg.getUser().getProfile().getProfileImagePath();
        this.content = chatMsg.getContent();
        this.edited = chatMsg.isEdited();
        this.fileUrls = chatFiles.stream()
                .map(cf -> cf.getFile().getPath()).toList();
        this.msgType = chatMsg.getMsgType();
        this.sendTime = chatMsg.getSendTime();
        this.unreadCount = unreadCount;

        if (chatMsg.getReplyToMsg() != null) {
            ChatMsg reply = chatMsg.getReplyToMsg();
            this.replyToMsgId = reply.getChatMsgId();
            this.replyToNickname = reply.getUser().getNickname();
            this.replyToContent = reply.getContent() != null ? reply.getContent() :
                    reply.getMsgType() == ChatMsgType.IMAGE ? "사진" : "동영상";
        }
    }
}
