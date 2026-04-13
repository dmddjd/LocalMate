package com.localmate.api.chat.service;

import com.localmate.api.chat.domain.*;
import com.localmate.api.chat.dto.ChatMsgRequestDto;
import com.localmate.api.chat.dto.ChatMsgResponseDto;
import com.localmate.api.chat.dto.ChatRoomListDto;
import com.localmate.api.chat.dto.CreateChatRoomResponseDto;
import com.localmate.api.chat.repository.ChatMsgRepository;
import com.localmate.api.chat.repository.ChatParticipantRepository;
import com.localmate.api.chat.repository.ChatRoomRepository;
import com.localmate.api.global.exception.CustomException;
import com.localmate.api.user.domain.User;
import com.localmate.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMsgRepository chatMsgRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final UserRepository userRepository;

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
        ChatMsg chatMsg = new ChatMsg(chatRoom, user, msgType, null, dto.getContent());
        chatMsgRepository.save(chatMsg);

        chatParticipantRepository.findActiveParticipant(chatRoomId, userId)
                .ifPresent(p -> p.updateLastRead(chatMsg.getChatMsgId()));

        String lastMsgContent = switch (chatMsg.getMsgType()) {
            case TEXT -> dto.getContent();
            case IMAGE -> "사진을 보냈습니다.";
            case VIDEO -> "동영상을 보냈습니다.";
        };
        chatRoom.updateLastMsg(lastMsgContent);

        int unreadCount = chatParticipantRepository.countActiveParticipants(chatRoomId) - 1;
        return new ChatMsgResponseDto(chatMsg, unreadCount);
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

        List<ChatMsg> messages = chatMsgRepository.findAllByChatRoomId(chatRoomId);

        if (!messages.isEmpty()) {
            ChatParticipant participant = chatParticipantRepository.findActiveParticipant(chatRoomId, userId)
                    .orElseThrow();
            participant.updateLastRead(messages.get(messages.size() - 1).getChatMsgId());
        }

        return messages.stream().map(ChatMsgResponseDto::new).toList();
    }
}
