package com.localmate.api.global.scheduler;

import com.localmate.api.chat.domain.ChatFile;
import com.localmate.api.chat.domain.ChatMsg;
import com.localmate.api.chat.domain.ChatRoom;
import com.localmate.api.chat.repository.*;
import com.localmate.api.global.file.domain.File;
import com.localmate.api.global.file.repository.FileRepository;
import com.localmate.api.user.domain.User;
import com.localmate.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountCleanupScheduler {
    private final FileRepository fileRepository;
    private final ChatFileRepository chatFileRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMsgRepository chatMsgRepository;
    private final ChatMsgHistoryRepository chatMsgHistoryRepository;
    private final ChatMsgHiddenRepository chatMsgHiddenRepository;
    private final ChatParticipantRepository chatParticipantRepository;
//    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteExpiredAccounts() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
        List<Long> targets = userRepository.findAllDeletedUser(cutoff)
                .stream()
                .map(User::getUserId)
                .toList();

        if(targets.isEmpty()) return;

        List<ChatFile> accountChatFiles = chatFileRepository.findAllByChatMsg_User_UserIdIn(targets);
        accountChatFiles.stream()
                .map(cf -> Paths.get(uploadDir, cf.getFile().getFileType().getSubDir(), cf.getFile().getChangeName()))
                .forEach(filePath -> {
                    try {
                        Files.deleteIfExists(filePath);
                    } catch (IOException e) {
                        log.warn("파일 삭제 실패: {}", filePath);
                    }
                });
        List<File> accountFiles = accountChatFiles.stream().map(ChatFile::getFile).toList();

        chatMsgHiddenRepository.deleteAllByUser_UserIdIn(targets);
        chatMsgHiddenRepository.deleteAllByChatMsg_User_UserIdIn(targets);
        chatMsgHistoryRepository.deleteAllByChatMsg_User_UserIdIn(targets);
        chatFileRepository.deleteAllByChatMsg_User_UserIdIn(targets);
        fileRepository.deleteAll(accountFiles);
        chatMsgRepository.deleteAllByUser_UserIdIn(targets);
        chatParticipantRepository.deleteAllByUser_UserIdIn(targets);
//        recommendationRepository.deleteAllByUser_UserIdIn(targets);
        userRepository.deleteAllById(targets);

        log.info("탈퇴 계정 {}건 삭제 완료", targets.size());
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteExpiredChatRooms() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
        List<Long> targets = chatRoomRepository.findAllDeletedChatRoom(cutoff)
                .stream()
                .map(ChatRoom::getChatRoomId)
                .toList();

        if (targets.isEmpty()) return;

        List<ChatFile> roomChatFiles = chatFileRepository.findAllByChatMsg_ChatRoom_ChatRoomIdIn(targets);
        roomChatFiles.stream()
                .map(cf -> Paths.get(uploadDir, cf.getFile().getFileType().getSubDir(), cf.getFile().getChangeName()))
                .forEach(filePath -> {
                    try {
                        Files.deleteIfExists(filePath);
                    } catch (IOException e) {
                        log.warn("파일 삭제 실패: {}", filePath);
                    }
                });
        List<File> roomFiles = roomChatFiles.stream().map(ChatFile::getFile).toList();

        chatMsgHiddenRepository.deleteAllByChatMsg_ChatRoom_ChatRoomIdIn(targets);
        chatMsgHistoryRepository.deleteAllByChatMsg_ChatRoom_ChatRoomIdIn(targets);
        chatFileRepository.deleteAllByChatMsg_ChatRoom_ChatRoomIdIn(targets);
        fileRepository.deleteAll(roomFiles);
        chatMsgRepository.deleteAllByChatRoom_ChatRoomIdIn(targets);
        chatParticipantRepository.deleteAllByChatRoom_ChatRoomIdIn(targets);
        chatRoomRepository.deleteAllById(targets);

        log.info("만료 채팅방 {}건 삭제 완료", targets.size());
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteExpiredFiles() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
        List<File> targets = fileRepository.findAllDeletedFile(cutoff);

        if (targets.isEmpty()) return;

        targets.stream()
                .map(file -> Paths.get(uploadDir, file.getFileType().getSubDir(), file.getChangeName()))
                .forEach(filePath -> {
                    try {
                        Files.deleteIfExists(filePath);
                    } catch (IOException e) {
                        log.warn("파일 삭제 실패 : {}", filePath);
                    }
                });

        fileRepository.deleteAll(targets);
        log.info("만료 파일 {}건 삭제 완료", targets.size());
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteExpiredChatMsgs() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);

        List<ChatMsg> expiredMsgs = chatMsgRepository.findAllExpiredDeletedMsg(cutoff);
        if (expiredMsgs.isEmpty()) return;

        List<Long> targets = expiredMsgs.stream().map(ChatMsg::getChatMsgId).toList();

        List<ChatFile> chatFiles = chatFileRepository.findAllByChatMsg_ChatMsgIdIn(targets);

        chatFiles.stream().map(cf -> Paths.get(uploadDir, cf.getFile().getFileType().getSubDir(), cf.getFile().getChangeName()))
                        .forEach(filePath -> {
                            try {
                                Files.deleteIfExists(filePath);
                            } catch (IOException e) {
                                log.warn("파일 삭제 실패 : {}", filePath);
                            }
                        });

        List<File> files = chatFiles.stream().map(ChatFile::getFile).toList();

        chatMsgHiddenRepository.deleteAllByChatMsg_ChatMsgIdIn(targets);
        chatMsgHistoryRepository.deleteAllByChatMsg_ChatMsgIdIn(targets);
        chatFileRepository.deleteAllByChatMsg_ChatMsgIdIn(targets);
        fileRepository.deleteAll(files);
        chatMsgRepository.deleteAllById(targets);

        log.info("만료 메시지 {}건 영구 삭제 완료", targets.size());
    }
}
