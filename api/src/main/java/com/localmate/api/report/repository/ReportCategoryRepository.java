package com.localmate.api.report.repository;

import com.localmate.api.report.domain.Category;
import com.localmate.api.report.domain.ReportCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportCategoryRepository extends JpaRepository<ReportCategory, Long> {
    void deleteAllByReport_ReportedIdIn(List<Long>reportedIds);
}
