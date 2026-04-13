package com.localmate.api.chat.controller;

import com.localmate.api.chat.dto.*;
import com.localmate.api.chat.service.ChatService;
import com.localmate.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/create")
    @Operation(summary = "채팅방 생성", description = "상대방과의 1대1 채팅방을 생성합니다")
    public ResponseEntity<ApiResponse<CreateChatRoomResponseDto>> createChatRoom(
            @RequestBody CreateChatRoomRequestDto request,
            @AuthenticationPrincipal Long userId
            ) {
        return ResponseEntity.ok(ApiResponse.success("채팅방 생성 성공!", chatService.createChatRoom(userId, request.getTargetUserId())));
    }

    @DeleteMapping("/{chatRoomId}")
    @Operation(summary = "채팅방 나가기", description = "채팅방에서 나갑니다.")
    public ResponseEntity<ApiResponse<Void>> leaveChatRoom(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal Long userId
    ) {
        String nickname = chatService.leaveChatRoom(chatRoomId, userId);
        messagingTemplate.convertAndSend("/topic/chat/" + chatRoomId,
                new SystemMsgResponseDto(nickname));

        return ResponseEntity.ok(ApiResponse.success("채팅방을 나갔습니다.", null));
    }

    @MessageMapping("/send/{chatRoomId}")
    public void sendMsg(@DestinationVariable Long chatRoomId,
                        // AuthenticationPrincipal : Http 필터 기반이기 때문에 WebSocket에서 동작하지 않음
                        // @AuthenticationPrincipal String id,
                        // Principal : StompHandler에서 accessor.setUser(auth)로 저장한 인증 객체를 가져옴
                        Authentication authentication,
                        @Payload ChatMsgRequestDto dto) {

        Long userId = (Long) authentication.getPrincipal();
        ChatMsgResponseDto response = chatService.sendMsg(chatRoomId, userId, dto);
        messagingTemplate.convertAndSend("/topic/chat/" + chatRoomId, response);
    }

    @GetMapping("/rooms")
    @Operation(summary = "채팅방 목록 조회")
    public ResponseEntity<ApiResponse<List<ChatRoomListDto>>> getRooms(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.success("채팅방 목록 조회 성공", chatService.getRooms(userId)));
    }

    @GetMapping("/{chatRoomId}/messages")
    @Operation(summary = "채팅 내역 조회")
    public ResponseEntity<ApiResponse<List<ChatMsgResponseDto>>> getMessages(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal Long userId) {
        List<ChatMsgResponseDto> messages = chatService.getMessages(chatRoomId, userId);

        messagingTemplate.convertAndSend("/topic/chat/" + chatRoomId, Map.of("type", "READ", "userId", userId));

        return ResponseEntity.ok(ApiResponse.success("채팅 내역 조회 성공", messages));
    }
}
