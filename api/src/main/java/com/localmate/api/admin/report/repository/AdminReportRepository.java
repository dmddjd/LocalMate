package com.localmate.api.admin.report.repository;

import com.localmate.api.report.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AdminReportRepository extends JpaRepository<Report, Long> {
    // 모든 신고 조회
    @Query("select r from Report r " +
            "join fetch r.reporter")
    List<Report> getAllReport();

    // 신고 상세 조회
    @Query("select r from Report r " +
            "join fetch r.reporter " +
            "join fetch r.categories rc " +
            "join fetch rc.category " +
            "where r.reportId = :reportId")
    Optional<Report> getReportDetail(@Param("reportId") Long reportId);
}
