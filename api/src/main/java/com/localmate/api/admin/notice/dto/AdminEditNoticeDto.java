package com.localmate.api.admin.notice.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class AdminEditNoticeDto {
    private String title;
    private String content;
    private boolean important;
    private LocalDateTime expireDate;
    private List<Long> deleteFileIds;
}
