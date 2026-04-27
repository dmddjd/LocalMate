package com.localmate.api.global.scheduler;

import com.localmate.api.admin.notice.domain.AdminNotice;
import com.localmate.api.admin.notice.repository.AdminNoticeFileRepository;
import com.localmate.api.admin.notice.repository.AdminNoticeRepository;
import com.localmate.api.chat.domain.ChatMsg;
import com.localmate.api.chat.domain.ChatRoom;
import com.localmate.api.chat.repository.*;
import com.localmate.api.global.file.domain.File;
import com.localmate.api.global.file.repository.FileRepository;
import com.localmate.api.global.file.service.FileService;
import com.localmate.api.report.repository.ReportCategoryRepository;
import com.localmate.api.report.repository.ReportRepository;
import com.localmate.api.user.domain.Recommendation;
import com.localmate.api.user.domain.User;
import com.localmate.api.user.repository.RecommendationRepository;
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
    private final FileService fileService;
    private final ChatFileRepository chatFileRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMsgRepository chatMsgRepository;
    private final ChatMsgHistoryRepository chatMsgHistoryRepository;
    private final ChatMsgHiddenRepository chatMsgHiddenRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final RecommendationRepository recommendationRepository;
    private final AdminNoticeRepository adminNoticeRepository;
    private final AdminNoticeFileRepository adminNoticeFileRepository;
    private final ReportCategoryRepository reportCategoryRepository;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteExpiredAccounts() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        List<Long> targets = userRepository.findAllDeletedUser(cutoff)
                .stream()
                .map(User::getUserId)
                .toList();

        if(targets.isEmpty()) return;

        // 내가 추천한 기록 삭제 + 상대방 추천 수 감소
        List<Recommendation> myRecommendations = recommendationRepository.findAllByFromUser_UserIdIn(targets);
        myRecommendations.forEach( r -> r.getToUser().getProfile().cancelRecommendation());
        recommendationRepository.deleteAllByFromUser_UserIdIn(targets);

        // 나를 추천한 기록 삭제
        recommendationRepository.deleteAllByToUser_UserIdIn(targets);

        chatMsgHiddenRepository.deleteAllByUser_UserIdIn(targets);
        chatMsgHiddenRepository.deleteAllByChatMsg_User_UserIdIn(targets);
        chatMsgHistoryRepository.deleteAllByChatMsg_User_UserIdIn(targets);
        chatFileRepository.deleteAllByChatMsg_User_UserIdIn(targets);
        chatMsgRepository.deleteAllByUser_UserIdIn(targets);
        chatParticipantRepository.deleteAllByUser_UserIdIn(targets);
        reportCategoryRepository.deleteAllByReport_ReportedIdIn(targets);
        reportRepository.deleteAllByReportedIdIn(targets);
        userRepository.deleteAllById(targets);

        log.info("탈퇴 계정 {}건 삭제 완료", targets.size());
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteExpiredChatRooms() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        List<Long> targets = chatRoomRepository.findAllDeletedChatRoom(cutoff)
                .stream()
                .map(ChatRoom::getChatRoomId)
                .toList();

        if (targets.isEmpty()) return;

        chatMsgHiddenRepository.deleteAllByChatMsg_ChatRoom_ChatRoomIdIn(targets);
        chatMsgHistoryRepository.deleteAllByChatMsg_ChatRoom_ChatRoomIdIn(targets);
        chatFileRepository.deleteAllByChatMsg_ChatRoom_ChatRoomIdIn(targets);
        chatMsgRepository.deleteAllByChatRoom_ChatRoomIdIn(targets);
        chatParticipantRepository.deleteAllByChatRoom_ChatRoomIdIn(targets);
        chatRoomRepository.deleteAllById(targets);

        log.info("만료 채팅방 {}건 삭제 완료", targets.size());
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteExpiredChatMsgs() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);

        List<ChatMsg> expiredMsgs = chatMsgRepository.findAllExpiredDeletedMsg(cutoff);
        if (expiredMsgs.isEmpty()) return;

        List<Long> targets = expiredMsgs.stream().map(ChatMsg::getChatMsgId).toList();

        chatMsgHiddenRepository.deleteAllByChatMsg_ChatMsgIdIn(targets);
        chatMsgHistoryRepository.deleteAllByChatMsg_ChatMsgIdIn(targets);
        chatFileRepository.deleteAllByChatMsg_ChatMsgIdIn(targets);
        chatMsgRepository.deleteAllById(targets);

        log.info("만료 메시지 {}건 영구 삭제 완료", targets.size());
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void expiredNotices() {
        List<AdminNotice> expiredNotices = adminNoticeRepository.findAllExpiredNotices(LocalDateTime.now());

        if (expiredNotices.isEmpty()) return;

        expiredNotices.forEach(
                notice -> {
                    notice.getAdminNoticeFiles().forEach(nf -> fileService.delete(nf.getFile()));
                    notice.deleteNotice();
                }
        );

        log.info("만료 공지 {}건 삭제 처리 완료", expiredNotices.size());
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteExpiredNotices() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        List<AdminNotice> notices = adminNoticeRepository.findAllDeletedNotices(cutoff);

        if (notices.isEmpty()) return;

        notices.forEach(
                notice -> adminNoticeFileRepository.deleteAll(notice.getAdminNoticeFiles()));

        adminNoticeRepository.deleteAll(notices);

        log.info("만료 공지 {}건 영구 삭제 완료", notices.size());
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteExpiredFiles() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
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
}
