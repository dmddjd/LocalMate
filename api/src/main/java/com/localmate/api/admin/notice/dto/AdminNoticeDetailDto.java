package com.localmate.api.admin.notice.dto;

import com.localmate.api.admin.notice.domain.AdminNotice;
import com.localmate.api.global.file.domain.File;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class AdminNoticeDetailDto {
    private Long noticeId;
    private String title;
    private String content;
    private boolean important;
    private LocalDateTime noticeDate;
    private LocalDateTime editDate;
    private LocalDateTime expireDate;
    private LocalDateTime deletedDate;
    private List<NoticeFileDto>  files;

    public AdminNoticeDetailDto(AdminNotice notice) {
        this.noticeId = notice.getNoticeId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.important = notice.isImportant();
        this.noticeDate = notice.getNoticeDate();
        this.editDate = notice.getEditDate();
        this.expireDate = notice.getExpireDate();
        this.deletedDate = notice.getDeleteDate();
        this.files = notice.getAdminNoticeFiles().stream().map(
                nf -> new NoticeFileDto(nf.getFile())).toList();
    }

    @Getter
    public static class NoticeFileDto {
        private Long fileId;
        private String originalName;

        public NoticeFileDto(File file) {
            this.fileId = file.getFileId();
            this.originalName = file.getOriginalName();
        }
    }
}
