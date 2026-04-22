package com.localmate.api.report.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ReportCategory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public ReportCategory(Report report, Category category) {
        this.report = report;
        this.category = category;
    }
}
