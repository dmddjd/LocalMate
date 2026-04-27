package com.localmate.api.notice.service;

import com.localmate.api.admin.notice.domain.AdminNotice;
import com.localmate.api.admin.notice.dto.AdminNoticeDetailDto;
import com.localmate.api.global.exception.CustomException;
import com.localmate.api.notice.dto.NoticeListDto;
import com.localmate.api.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;

    @Transactional(readOnly = true)
    public List<NoticeListDto> getNotice() {
        return noticeRepository.getNotice().stream().map(NoticeListDto::new).toList();
    }

    @Transactional(readOnly = true)
    public AdminNoticeDetailDto getNoticeDetail(Long noticeId) {
        AdminNotice notice = noticeRepository.getNoticeDetail(noticeId).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 공지입니다."));
        return new AdminNoticeDetailDto(notice);
    }
}
