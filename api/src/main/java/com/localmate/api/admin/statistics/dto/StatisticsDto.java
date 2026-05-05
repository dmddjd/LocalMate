package com.localmate.api.admin.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StatisticsDto {
    private String period;
    private long count;
}
