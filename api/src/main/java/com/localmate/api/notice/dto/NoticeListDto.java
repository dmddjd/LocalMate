package com.localmate.api.notice.dto;

import com.localmate.api.admin.notice.domain.AdminNotice;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoticeListDto {
    private Long noticeId;
    private String title;
    private boolean important;
    private LocalDateTime noticeDate;

    public NoticeListDto(AdminNotice notice) {
        this.noticeId = notice.getNoticeId();
        this.title = notice.getTitle();
        this.important = notice.isImportant();
        this.noticeDate = notice.getNoticeDate();
    }
}
