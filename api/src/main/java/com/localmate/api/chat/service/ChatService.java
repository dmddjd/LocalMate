package com.localmate.api.chat.service;

import com.localmate.api.chat.domain.*;
import com.localmate.api.chat.dto.*;
import com.localmate.api.chat.repository.*;
import com.localmate.api.global.exception.CustomException;
import com.localmate.api.global.file.domain.File;
import com.localmate.api.global.file.domain.FileType;
import com.localmate.api.global.file.repository.FileRepository;
import com.localmate.api.global.file.service.FileService;
import com.localmate.api.user.domain.User;
import com.localmate.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMsgRepository chatMsgRepository;
    private final ChatFileRepository chatFileRepository;
    private final ChatMsgHistoryRepository chatMsgHistoryRepository;
    private final ChatMsgHiddenRepository chatMsgHiddenRepository;
    private final FileService fileService;
    private final FileRepository fileRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final UserRepository userRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public CreateChatRoomResponseDto createChatRoom(Long userId, Long targetUserId) {

        // 1. 유저 조회
        User loginUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        User targetUser = userRepository.findByUserId(targetUserId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        // 2. 기존 채팅방 존재 여부 확인
        Optional<ChatRoom> existingRoom =
                chatRoomRepository.findExistRoom(loginUser.getUserId(), targetUserId);

        if (existingRoom.isPresent()) {
            return new CreateChatRoomResponseDto(
                    existingRoom.get().getChatRoomId(),
                    true
            );
        }

        // 3. 채팅방 생성
        ChatRoom chatRoom = new ChatRoom();
        chatRoomRepository.save(chatRoom);

        // 4. 참여자 저장
        ChatParticipant p1 = new ChatParticipant(chatRoom, loginUser);
        ChatParticipant p2 = new ChatParticipant(chatRoom, targetUser);

        chatParticipantRepository.save(p1);
        chatParticipantRepository.save(p2);

        return new CreateChatRoomResponseDto(
                chatRoom.getChatRoomId(),
                false
        );
    }

    @Transactional
    public ChatMsgResponseDto sendMsg(Long chatRoomId, Long userId, ChatMsgRequestDto dto) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 채팅방입니다."));

        if (!chatParticipantRepository.existActiveParticipant(chatRoomId, userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "채팅방 참여자가 아닙니다.");
        }

        ChatMsgType msgType = dto.getMsgType() != null ? dto.getMsgType() : ChatMsgType.TEXT;

        ChatMsg replyToMsg = null;
        if (dto.getReplyToMsgId() != null) {
            replyToMsg = chatMsgRepository.findById(dto.getReplyToMsgId())
                    .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "답장 대상 메시지가 존재하지 않습니다."));
        }

        ChatMsg chatMsg = new ChatMsg(chatRoom, user, msgType, dto.getContent(), replyToMsg);
        chatMsgRepository.save(chatMsg);

        chatParticipantRepository.findActiveParticipant(chatRoomId, userId)
                .ifPresent(p -> p.updateLastRead(chatMsg.getChatMsgId()));

        chatRoom.updateLastMsg(dto.getContent());

        int unreadCount = chatParticipantRepository.countActiveParticipants(chatRoomId) - 1;
        return new ChatMsgResponseDto(chatMsg, unreadCount);
    }

    @Transactional
    public ChatMsgResponseDto sendFileMsg(Long chatRoomId, Long userId, List<MultipartFile> files, ChatMsgType msgType, Long replyToMsgId) {
        User user = userRepository.findByUserId(userId).orElseThrow(() ->
                new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() ->
                new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 채팅방입니다."));

        if (!chatParticipantRepository.existActiveParticipant(chatRoomId, userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "채팅방 참여자가 아닙니다.");
        }

        ChatMsg replyToMsg = null;
        if (replyToMsgId != null) {
            replyToMsg = chatMsgRepository.findById(replyToMsgId)
                    .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "답장 대상 메시지가 존재하지 않습니다."));
        }

        ChatMsg chatMsg = new ChatMsg(chatRoom, user, msgType, replyToMsg);
        chatMsgRepository.save(chatMsg);

        List<ChatFile> chatFiles = files.stream().map(
                f -> new ChatFile(chatMsg, fileService.upload(f, FileType.CHAT))).toList();

        chatFileRepository.saveAll(chatFiles);

        chatParticipantRepository.findActiveParticipant(chatRoomId, userId).ifPresent(
                p -> p.updateLastRead(chatMsg.getChatMsgId()));

        String lastMsgContent = msgType == ChatMsgType.IMAGE ? "사진을 보냈습니다." : "동영상을 보냈습니다.";
        chatRoom.updateLastMsg(lastMsgContent);

        int unreadCount = chatParticipantRepository.countActiveParticipants(chatRoomId) - 1;

        return new ChatMsgResponseDto(chatMsg, unreadCount, chatFiles);
    }

    public record FileDownloadInfo(Resource resource, String originalName) {};

    @Transactional(readOnly = true)
    public FileDownloadInfo getFileAsResource(String fileUrl, Long userId) {
        String changeName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
        File file = fileRepository.findByChangeName(changeName)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."));

        ChatFile chatFile = chatFileRepository.findByFile_ChangeName(changeName)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."));

        Long chatRoomId = chatFile.getChatMsg().getChatRoom().getChatRoomId();
        if (!chatParticipantRepository.existActiveParticipant(chatRoomId, userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
        }

        String relativePath = fileUrl.replaceFirst("^/images/", "");
        Path filePath = Paths.get(uploadDir, relativePath);
        Resource resource = new FileSystemResource(filePath);
        if (!resource.exists()) throw new CustomException(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다.");

        return new FileDownloadInfo(resource, file.getOriginalName());
    }

    @Transactional
    public void editMsg(Long chatRoomId, Long chatMsgId, Long userId, String newContent) {
        ChatMsg chatMsg = chatMsgRepository.findById(chatMsgId).orElseThrow(() ->
                new CustomException(HttpStatus.NOT_FOUND,"메시지가 존재하지 않습니다."));

        if (!chatMsg.getUser().getUserId().equals(userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "본인의 메시지만 수정할 수 있습니다.");
        }

        if (!chatMsg.getChatRoom().getChatRoomId().equals(chatRoomId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "해당 채팅방의 메시지가 아닙니다.");
        }

        if (chatMsg.getMsgType() != ChatMsgType.TEXT) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "텍스트 메시지만 수정할 수 있습니다.");
        }

        chatMsgHistoryRepository.save(new ChatMsgHistory(chatMsg, chatMsg.getContent()));
        chatMsg.edit(newContent);
    }

    @Transactional
    public ChatNoticeResponseDto setNotice(Long chatRoomId, Long userId, Long chatMsgId) {
        if (!chatParticipantRepository.existActiveParticipant(chatRoomId, userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "채팅방 참여자가 아닙니다.");
        }

        ChatMsg chatMsg = chatMsgRepository.findById(chatMsgId).orElseThrow(() ->
                new CustomException(HttpStatus.NOT_FOUND, "메시지가 존재하지 않습니다."));

        chatParticipantRepository.findAllActiveParticipants(chatRoomId)
                .forEach(p -> p.setNotice(chatMsgId));

        return new ChatNoticeResponseDto(chatMsg);
    }

    @Transactional
    public ChatNoticeResponseDto getNotice(Long chatRoomId, Long userId) {
        ChatParticipant participant = chatParticipantRepository.findActiveParticipant(chatRoomId, userId)
                .orElseThrow(() -> new CustomException(HttpStatus.FORBIDDEN, "채팅방 참여자가 아닙니다."));

        if (participant.getNoticeMsgId() == null) return null;

        return chatMsgRepository.findById(participant.getNoticeMsgId())
                .map(ChatNoticeResponseDto::new)
                .orElse(null);
    }

    @Transactional
    public void deleteNotice(Long chatRoomId, Long userId) {
        chatParticipantRepository.findActiveParticipant(chatRoomId, userId)
                .ifPresent(ChatParticipant::clearNotice);
    }

    @Transactional
    public void deleteForAll(Long chatRoomId, Long chatMsgId, Long userId) {
        ChatMsg chatMsg = chatMsgRepository.findById(chatMsgId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "메시지가 존재하지 않습니다."));

        if (!chatMsg.getUser().getUserId().equals(userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "본인의 메시지만 삭제할 수 있습니다.");
        }

        if (!chatMsg.getChatRoom().getChatRoomId().equals(chatRoomId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "해당 채팅방의 메시지가 아닙니다.");
        }

        chatMsg.delete();

        ChatRoom chatRoom = chatMsg.getChatRoom();
        chatMsgRepository.findFirstByChatRoom_ChatRoomIdAndStatusOrderByChatMsgIdDesc(chatRoomId, ChatMsgStatus.ACTIVE)
                .ifPresentOrElse(latest -> {
                    String content = latest.getMsgType() == ChatMsgType.TEXT
                            ? latest.getContent() : (latest.getMsgType() == ChatMsgType.IMAGE ? "사진을 보냈습니다." : "동영상을 보냈습니다.");
                    chatRoom.updateLastMsg(content);
                },
                        () -> chatRoom.updateLastMsg("")
                );
    }

    @Transactional
    public void deleteForMe(Long chatRoomId, Long chatMsgId, Long userId) {
        ChatMsg chatMsg = chatMsgRepository.findById(chatMsgId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "메시지가 존재하지 않습니다."));

        if (!chatMsg.getChatRoom().getChatRoomId().equals(chatRoomId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "해당 채팅방의 메시지가 아닙니다.");
        }

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        chatMsgHiddenRepository.save(new ChatMsgHidden(chatMsg, user));

        int totalParticipants = chatParticipantRepository.countActiveParticipants(chatRoomId);
        long hiddenCount = chatMsgHiddenRepository.countByChatMsg_ChatMsgId(chatMsgId);

        if (hiddenCount >= totalParticipants) {
            chatMsg.delete();
        }
    }

    @Transactional
    public String leaveChatRoom(Long chatRoomId, Long userId) {
        ChatParticipant participant = chatParticipantRepository
                .findActiveParticipant(chatRoomId, userId)
                .orElseThrow(() -> new CustomException(HttpStatus.FORBIDDEN, "채팅방 참여자가 아닙니다."));

        participant.leave();

        if (!chatParticipantRepository.existAnyActiveParticipant(chatRoomId)) {
            participant.getChatRoom().close();
        }

        return participant.getUser().getNickname();
    }

    @Transactional(readOnly = true)
    public List<ChatRoomListDto> getRooms(Long userId) {
        List<ChatParticipant> myParticipations = chatParticipantRepository.findMyParticipation(userId);

        List<Long> chatRoomIds = myParticipations.stream()
                .map(cp -> cp.getChatRoom().getChatRoomId())
                .toList();

        Map<Long, ChatParticipant> opponentMap = chatParticipantRepository.findOpponents(chatRoomIds, userId).stream()
                .collect(Collectors.toMap(cp -> cp.getChatRoom().getChatRoomId(), cp -> cp));

        return myParticipations.stream().map(cp -> {
            ChatParticipant opponent = opponentMap.get(cp.getChatRoom().getChatRoomId());
            int unreadCount = chatMsgRepository.countUnread(cp.getChatRoom().getChatRoomId(),cp.getLastReadMsgId());
            return new ChatRoomListDto(cp.getChatRoom(), opponent, unreadCount);
        }).toList();
    }

    @Transactional
    public List<ChatMsgResponseDto> getMessages(Long chatRoomId, Long userId) {
        if(!chatParticipantRepository.existActiveParticipant(chatRoomId, userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "채팅방 참여자가 아닙니다.");
        }

        List<ChatMsg> messages = chatMsgRepository.findAllByChatRoomId(chatRoomId, userId);

        if (!messages.isEmpty()) {
            ChatParticipant participant = chatParticipantRepository.findActiveParticipant(chatRoomId, userId)
                    .orElseThrow();
            participant.updateLastRead(messages.get(messages.size() - 1).getChatMsgId());
        }

        Long opponentLastRead = chatParticipantRepository.findOpponentLastRead(chatRoomId, userId);

        return messages.stream().map(msg -> {
            int unreadCount = (opponentLastRead == null || opponentLastRead < msg.getChatMsgId()) ? 1 : 0;
            return new ChatMsgResponseDto(msg, unreadCount);
        }).toList();
    }
}
