package com.localmate.api.report.repository;

import com.localmate.api.report.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByReporter_UserIdAndReportedId(Long reporterId, Long reportedId);
    void deleteAllByReportedIdIn(List<Long> reportedIds);
}
