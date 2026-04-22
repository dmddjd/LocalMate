package com.localmate.api.report.repository;

import com.localmate.api.report.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportCategoryRepository extends JpaRepository<Category, Long> {
}
