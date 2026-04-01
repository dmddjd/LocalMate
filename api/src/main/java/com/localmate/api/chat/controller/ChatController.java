package com.localmate.api.chat.controller;

import com.localmate.api.chat.dto.ChatMsgRequestDto;
import com.localmate.api.chat.dto.ChatMsgResponseDto;
import com.localmate.api.chat.dto.CreateChatRoomRequestDto;
import com.localmate.api.chat.dto.CreateChatRoomResponseDto;
import com.localmate.api.chat.service.ChatService;
import com.localmate.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    @Operation(summary = "채팅방 생성", description = "상대방과의 1대1 채팅방을 생성합니다")
    public ResponseEntity<ApiResponse<CreateChatRoomResponseDto>> createChatRoom(
            @RequestBody CreateChatRoomRequestDto request,
            @AuthenticationPrincipal String id
            ) {
        return ResponseEntity.ok(ApiResponse.success("채팅방 생성 성공!", chatService.createChatRoom(id, request.getTargetUserId())));
    }

    @MessageMapping("/chat/{chatRoomId}")
    public void sendMsg(@DestinationVariable Long chatRoomId,
                        // AuthenticationPrincipal : Http 필터 기반이기 때문에 WebSocket에서 동작하지 않음
                        // @AuthenticationPrincipal String id,
                        // Principal : StompHandler에서 accessor.setUser(auth)로 저장한 인증 객체를 가져옴
                        Principal principal,
                        @Payload ChatMsgRequestDto dto) {
        ChatMsgResponseDto response = chatService.sendMsg(chatRoomId, principal.getName(), dto);
        messagingTemplate.convertAndSend("/topic/chat/" + chatRoomId, response);
    }
}
