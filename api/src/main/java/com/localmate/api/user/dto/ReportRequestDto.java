package com.localmate.api.user.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ReportRequestDto {
    private Long reportedId;
    private List<Long> categoryIds;
    private String description;
}
