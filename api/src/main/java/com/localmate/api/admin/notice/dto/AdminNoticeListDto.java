package com.localmate.api.admin.notice.dto;

import com.localmate.api.admin.notice.domain.AdminNotice;
import com.localmate.api.admin.notice.domain.NoticeStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AdminNoticeListDto {
    private Long noticeId;
    private String title;
    private boolean important;
    private LocalDateTime noticeDate;
    private NoticeStatus status;

    public AdminNoticeListDto(AdminNotice notice) {
        this.noticeId = notice.getNoticeId();
        this.title = notice.getTitle();
        this.important = notice.isImportant();
        this.noticeDate = notice.getNoticeDate();
        this.status = notice.getStatus();
    }
}
