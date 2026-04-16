package com.localmate.api.chat.controller;

import com.localmate.api.chat.domain.ChatMsgType;
import com.localmate.api.chat.dto.*;
import com.localmate.api.chat.service.ChatService;
import com.localmate.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
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

    @PostMapping("/{chatRoomId}/file")
    @Operation(summary = "파일 전송")
    public ResponseEntity<ApiResponse<Void>> sendFileMsg(
            @PathVariable Long chatRoomId,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("msgType") ChatMsgType msgType,
            @RequestParam(value = "replyToMsgId", required = false) Long replyToMsgId,
            @AuthenticationPrincipal Long userId
            ) {
        ChatMsgResponseDto response = chatService.sendFileMsg(chatRoomId, userId, files, msgType, replyToMsgId);
        messagingTemplate.convertAndSend("/topic/chat/" + chatRoomId, response);
        return ResponseEntity.ok(ApiResponse.success("파일 전송 성공", null));
    }

    @GetMapping("/files/download")
    public ResponseEntity<Resource> downloadFile(
            @RequestParam String fileUrl,
            @AuthenticationPrincipal Long userId
            ) {
        ChatService.FileDownloadInfo info = chatService.getFileAsResource(fileUrl, userId);

        String encodedName = UriUtils.encode(info.originalName(), StandardCharsets.UTF_8);
        String contentType = URLConnection.guessContentTypeFromName(info.originalName());
        if (contentType == null) contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName)
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(info.resource());
    }

    @PutMapping("/{chatRoomId}/messages/{chatMsgId}")
    @Operation(summary = "채팅 메세지 수정")
    public ResponseEntity<ApiResponse<Void>> editMsg(
            @PathVariable Long chatRoomId,
            @PathVariable Long chatMsgId,
            @RequestBody ChatMsgEditRequestDto request,
            @AuthenticationPrincipal Long userId
    ) {
        chatService.editMsg(chatRoomId, chatMsgId, userId, request.getContent());
        messagingTemplate.convertAndSend("/topic/chat/" + chatRoomId,
                Map.of("type", "EDIT", "chatMsgId", chatMsgId, "content", request.getContent()));

        return ResponseEntity.ok(ApiResponse.success("채팅 수정 성공", null));
    }

    @PostMapping("/{chatRoomId}/notice")
    @Operation(summary = "채팅 공지")
    public ResponseEntity<ApiResponse<Void>> setNotice(
            @PathVariable Long chatRoomId,
            @RequestParam Long chatMsgId,
            @AuthenticationPrincipal Long userId
    ) {
        ChatNoticeResponseDto notice = chatService.setNotice(chatRoomId, userId, chatMsgId);
        messagingTemplate.convertAndSend("/topic/chat/" + chatRoomId,
                Map.of("type", "NOTICE_SET", "chatMsgId", notice.getChatMsgId(), "content", notice.getContent()));

        return ResponseEntity.ok(ApiResponse.success("채팅 공지 성공", null));
    }

    @GetMapping("/{chatRoomId}/notice")
    @Operation(summary = "공지 조회")
    public ResponseEntity<ApiResponse<ChatNoticeResponseDto>> getNotice(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(ApiResponse.success("공지 조회 성공", chatService.getNotice(chatRoomId, userId)));
    }

    @DeleteMapping("/{chatRoomId}/notice")
    @Operation(summary = "공지 삭제")
    public ResponseEntity<ApiResponse<Void>> deleteNotice(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal Long userId
    ) {
        chatService.deleteNotice(chatRoomId, userId);
        return ResponseEntity.ok(ApiResponse.success("공지 삭제 성공", null));
    }

    @DeleteMapping("/{chatRoomId}/messages/{chatMsgId}")
    @Operation(summary = "채팅 모두에게서 삭제")
    public ResponseEntity<ApiResponse<Void>> deleteForAll(
            @PathVariable Long chatRoomId,
            @PathVariable Long chatMsgId,
            @AuthenticationPrincipal Long userId
    ) {
        chatService.deleteForAll(chatRoomId, chatMsgId, userId);
        messagingTemplate.convertAndSend("/topic/chat/" + chatRoomId, Map.of("type", "DELETE", "chatMsgId", chatMsgId));
        return ResponseEntity.ok(ApiResponse.success("삭제 성공", null));
    }

    @DeleteMapping("/{chatRoomId}/messages/{chatMsgId}/me")
    @Operation(summary = "채팅 나에게서만 삭제")
    public ResponseEntity<ApiResponse<Void>> deleteForMe(
            @PathVariable Long chatRoomId,
            @PathVariable Long chatMsgId,
            @AuthenticationPrincipal Long userId
    ) {
        chatService.deleteForMe(chatRoomId, chatMsgId, userId);
        return ResponseEntity.ok(ApiResponse.success("삭제 성공", null));
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
