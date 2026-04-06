package com.localmate.api.global.scheduler;

import com.localmate.api.chat.repository.ChatMsgRepository;
import com.localmate.api.chat.repository.ChatParticipantRepository;
import com.localmate.api.global.file.domain.File;
import com.localmate.api.global.file.repository.FileRepository;
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
    private final ChatMsgRepository chatMsgRepository;
    private final ChatParticipantRepository chatParticipantRepository;
//    private final RecommendationRepository recommendationRepository;
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

        chatMsgRepository.deleteAllByUser_UserIdIn(targets);
        chatParticipantRepository.deleteAllByUser_UserIdIn(targets);
//        recommendationRepository.deleteAllByUser_UserIdIn(targets);
        userRepository.deleteAllById(targets);

        log.info("탈퇴 계정 {}건 삭제 완료", targets.size());
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteExpiredFiles() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(10);
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
