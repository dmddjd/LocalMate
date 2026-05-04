package com.localmate.api.user.repository;

import com.localmate.api.user.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByReporter_UserIdAndReportedId(Long reporterId, Long reportedId);
    void deleteAllByReportedIdIn(List<Long> reportedIds);
}
