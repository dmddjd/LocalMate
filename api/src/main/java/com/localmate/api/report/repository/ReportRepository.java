package com.localmate.api.report.repository;

import com.localmate.api.report.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByReporter_UserIdAndReportedId(Long reporterId, Long reportedId);
}
