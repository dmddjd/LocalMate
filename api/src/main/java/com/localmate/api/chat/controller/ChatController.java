package com.localmate.api.chat.controller;

import com.localmate.api.chat.dto.CreateChatRoomRequestDto;
import com.localmate.api.chat.dto.CreateChatRoomResponseDto;
import com.localmate.api.chat.service.ChatService;
import com.localmate.api.global.response.ApiResponse;
import com.localmate.api.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;

    @PostMapping
    @Operation(summary = "채팅방 만들기", description = "상대방과의 1대1 채팅방을 생성합니다")
    public ResponseEntity<ApiResponse<CreateChatRoomResponseDto>> createChatRoom(
            @RequestBody CreateChatRoomRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) {
        Long loginUserId = userDetails.getUser().getUserId();

        return ResponseEntity.ok(ApiResponse.success("채팅방 생성 성공!", chatService.createChatRoom(loginUserId, request.getTargetUserId())));
    }
}
