package com.localmate.api.report.controller;

import com.localmate.api.global.response.ApiResponse;
import com.localmate.api.report.dto.ReportRequestDto;
import com.localmate.api.report.service.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
@Tag(name = "Report Controller", description = "신고 API 입니다.")
public class ReportController {
    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> report(
            @RequestBody ReportRequestDto request,
            @AuthenticationPrincipal Long userId
            ) {
        reportService.report(userId, request);
        return ResponseEntity.ok(ApiResponse.success("신고가 접수되었습니다.",null));

    }
}
