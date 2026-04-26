package com.localmate.api.admin.notice.service;

import com.localmate.api.admin.notice.domain.AdminNotice;
import com.localmate.api.admin.notice.domain.AdminNoticeFile;
import com.localmate.api.admin.notice.dto.AdminCreateNoticeDto;
import com.localmate.api.admin.notice.dto.AdminEditNoticeDto;
import com.localmate.api.admin.notice.dto.AdminNoticeListDto;
import com.localmate.api.admin.notice.repository.AdminNoticeFileRepository;
import com.localmate.api.admin.notice.repository.AdminNoticeRepository;
import com.localmate.api.global.exception.CustomException;
import com.localmate.api.global.file.domain.FileType;
import com.localmate.api.global.file.service.FileService;
import com.localmate.api.user.domain.User;
import com.localmate.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminNoticeService {
    private final UserRepository userRepository;
    private final AdminNoticeRepository adminNoticeRepository;
    private final AdminNoticeFileRepository adminNoticeFileRepository;
    private final FileService fileService;

    @Transactional
    public void createNotice(Long userId, AdminCreateNoticeDto dto, List<MultipartFile> files) {
        User author = userRepository.findByUserId(userId).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        AdminNotice notice = new AdminNotice(author, dto.getTitle(), dto.getContent(), dto.isImportant(), dto.getExpireDate());
        adminNoticeRepository.save(notice);

        if (files != null && !files.isEmpty()) {
            List<AdminNoticeFile> adminNoticeFiles = files.stream().map(
                    f -> new AdminNoticeFile(notice, fileService.upload(f, FileType.NOTICE))).toList();
            adminNoticeFileRepository.saveAll(adminNoticeFiles);
        }
    }

    @Transactional(readOnly = true)
    public List<AdminNoticeListDto> getAllNotice() {
        return adminNoticeRepository.getAllNotice().stream().map(AdminNoticeListDto::new).toList();
    }

    @Transactional
    public void editNotice(Long noticeId, AdminEditNoticeDto dto, List<MultipartFile> files) {
        AdminNotice notice = adminNoticeRepository.findById(noticeId).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 공지입니다."));

        notice.editNotice(dto.getTitle(), dto.getContent(), dto.isImportant(), dto.getExpireDate());

        if (dto.getDeleteFileIds() != null && !dto.getDeleteFileIds().isEmpty()) {
            List<AdminNoticeFile> toDelete = adminNoticeFileRepository.findAllById(dto.getDeleteFileIds());
            toDelete.forEach(nf -> fileService.delete(nf.getFile()));
            adminNoticeFileRepository.deleteAll(toDelete);
        }

        if (files != null && !files.isEmpty()) {
            List<AdminNoticeFile> newFiles = files.stream().map(
                    f -> new AdminNoticeFile(notice, fileService.upload(f, FileType.NOTICE))).toList();
            adminNoticeFileRepository.saveAll(newFiles);
        }
    }

    @Transactional
    public void deleteNotice(Long noticeId) {
        AdminNotice notice = adminNoticeRepository.findById(noticeId).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 공지입니다."));
        notice.deleteNotice();
    }
}
