package com.localmate.api.report.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ReportRequestDto {
    private Long reportedId;
    private List<Long> categoryIds;
    private String description;
}
