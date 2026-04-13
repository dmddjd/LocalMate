package com.localmate.api.chat.dto;

import com.localmate.api.chat.domain.ChatParticipant;
import com.localmate.api.chat.domain.ChatRoom;
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

    public ChatRoomListDto(ChatRoom chatRoom, ChatParticipant opponent, int unreadMsgCount) {
        this.chatRoomId = chatRoom.getChatRoomId();
        this.lastMsg = chatRoom.getLastMsgContent();
        this.lastMsgDate = chatRoom.getLastMsgDate();
        this.unreadMsgCount = unreadMsgCount;
        if (opponent != null) {
           this.opponentId = opponent.getUser().getUserId();
           this.opponentNickname = opponent.getUser().getNickname();
           this.opponentProfileImage = opponent.getUser().getProfile().getProfileImagePath();
        }
    }
}
