package com.localmate.api.admin.report.dto;

import com.localmate.api.report.domain.Report;
import com.localmate.api.report.domain.ReportStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AdminReportListDto {
    private Long reportId;
    private LocalDateTime reportDate;
    private String reporterNickname;
    private ReportStatus status;

    public AdminReportListDto(Report report) {
        this.reportId = report.getReportId();
        this.reportDate = report.getReportDate();
        this.reporterNickname = report.getReporter().getNickname();
        this.status = report.getStatus();
    }
}
