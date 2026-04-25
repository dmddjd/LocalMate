package com.localmate.api.admin.report.dto;

import com.localmate.api.report.domain.Report;
import com.localmate.api.report.domain.ReportStatus;
import com.localmate.api.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class AdminReportDetailDto {
    private Long reportId;
    private Long reporterUserId;
    private String reporterNickname;
    private Long reportedUserId;
    private String reportedNickname;
    private List<String> categories;
    private String description;
    private ReportStatus status;
    private LocalDateTime reportDate;

    public AdminReportDetailDto(Report report, User reportedUser) {
        this.reportId = report.getReportId();
        this.reporterUserId = report.getReporter().getUserId();
        this.reporterNickname = report.getReporter().getNickname();
        this.reportedUserId = reportedUser.getUserId();
        this.reportedNickname = reportedUser.getNickname();
        this.categories = report.getCategories().stream().map(rc -> rc.getCategory().getCategory()).toList();
        this.description = report.getDescription();
        this.status = report.getStatus();
        this.reportDate = report.getReportDate();


    }
}
