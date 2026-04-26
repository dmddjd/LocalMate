package com.localmate.api.admin.report.controller;

import com.localmate.api.admin.report.dto.AdminReportDetailDto;
import com.localmate.api.admin.report.dto.AdminReportListDto;
import com.localmate.api.admin.report.service.AdminReportService;
import com.localmate.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/report")
@Tag(name = "Admin - Report Controller", description = "관리자 - 신고 API 입니다.")
public class AdminReportController {
    private final AdminReportService adminReportService;

    @GetMapping
    @Operation(summary = "신고 목록 조회", description = "모든 신고 내역을 조회합니다.")
    public ResponseEntity<ApiResponse<List<AdminReportListDto>>> getAllReport() {
        return ResponseEntity.ok(ApiResponse.success("신고 목록 조회 성공", adminReportService.getAllReport()));
    }

    @GetMapping("/{reportId}")
    @Operation(summary = "신고 상세 조회", description = "신고 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<AdminReportDetailDto>> getReportDetail(@PathVariable Long reportId) {
        return ResponseEntity.ok(ApiResponse.success("신고 상세 정보 조회 성공", adminReportService.getReportDetail(reportId)));
    }

    @PatchMapping("/{reportId}/sanction")
    @Operation(summary = "신고 제재 처리", description = "신고 상태를 변경합니다.")
    public ResponseEntity<ApiResponse<Void>> sanction(@PathVariable Long reportId) {
        adminReportService.sanction(reportId);
        return ResponseEntity.ok(ApiResponse.success("제재 처리 성공", null));
    }

    @PatchMapping("/{reportId}/reject")
    @Operation(summary = "신고 기각 처리", description = "신고 상태를 변경합니다.")
    public ResponseEntity<ApiResponse<Void>> reject(@PathVariable Long reportId) {
        adminReportService.reject(reportId);
        return ResponseEntity.ok(ApiResponse.success("기각 처리 성공", null));
    }

}
