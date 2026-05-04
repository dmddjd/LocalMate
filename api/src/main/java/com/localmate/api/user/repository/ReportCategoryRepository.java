package com.localmate.api.user.repository;

import com.localmate.api.user.domain.ReportCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportCategoryRepository extends JpaRepository<ReportCategory, Long> {
    void deleteAllByReport_ReportedIdIn(List<Long>reportedIds);
}
