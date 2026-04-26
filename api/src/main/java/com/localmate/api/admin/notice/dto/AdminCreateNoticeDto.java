package com.localmate.api.admin.notice.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AdminCreateNoticeDto {
    private String title;
    private String content;
    private boolean important;
    private LocalDateTime expireDate;
}
