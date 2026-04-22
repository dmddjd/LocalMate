package com.localmate.api.report.repository;

import com.localmate.api.report.domain.Report;
import com.localmate.api.report.domain.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByReporter_UserIdAndReportedIdAndReportType(Long reporterId, Long reportedId, ReportType reportType);
}
