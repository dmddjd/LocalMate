package com.localmate.api.report.dto;

import com.localmate.api.report.domain.ReportType;
import lombok.Getter;

import java.util.List;

@Getter
public class ReportRequestDto {
    private ReportType reportType;
    private Long reportedId;
    private List<Long> categoryIds;
    private String description;
}
