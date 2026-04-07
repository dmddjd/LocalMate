package com.localmate.api.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class SystemMsgResponseDto {
    private String content;

    public SystemMsgResponseDto(String nickname) {
        this.content = nickname + "님이 채팅방을 나갔습니다.";
    }
}
