package com.localmate.api.report.domain;

import com.localmate.api.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Report {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Column(nullable = false)
    private Long reportedId;

    @OneToMany(mappedBy = "report", cascade = CascadeType.PERSIST)
    private List<ReportCategory> categories = new ArrayList<>();

    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @Column(nullable = false)
    private LocalDateTime reportDate;

    public Report(User reporter, Long reportedId, List<Category> categories, String description) {
        this.reporter = reporter;
        this.reportedId = reportedId;
        this.categories = categories.stream().map(
                category -> new ReportCategory(this, category)).toList();
        this.description = description;
        this.status = ReportStatus.PENDING;
    }

    @PrePersist
    public void prePersist() {
        this.reportDate = LocalDateTime.now();
    }
}
