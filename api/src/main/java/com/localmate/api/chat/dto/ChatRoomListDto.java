package com.localmate.api.chat.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatRoomListDto {
    private Long chatRoomId;
    private String lastMsg;
    private LocalDateTime lastMsgDate;
    private int unreadMsgCount;
    private Long opponentId;
    private String opponentNickname;
    private String opponentProfileImage;
}
