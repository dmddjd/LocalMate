package com.localmate.api.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateChatRoomResponseDto {
    private Long chatRoomId;
    private boolean alreadyExists;

}
