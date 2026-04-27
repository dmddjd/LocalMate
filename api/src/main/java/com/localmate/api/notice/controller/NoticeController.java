package com.localmate.api.notice.controller;

import com.localmate.api.admin.notice.dto.AdminNoticeDetailDto;
import com.localmate.api.global.response.ApiResponse;
import com.localmate.api.notice.dto.NoticeListDto;
import com.localmate.api.notice.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
@Tag(name = "Notice Controller", description = "공지 API 입니다.")
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping
    @Operation(summary = "공지 목록 조회", description = "공지 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<NoticeListDto>>> getNotice() {
        return ResponseEntity.ok(ApiResponse.success("공지 목록 조회 성공", noticeService.getNotice()));
    }

    @GetMapping("/{noticeId}")
    @Operation(summary = "공지 상세 조회", description = "공지 상세 정보를 조회입니다.")
    public ResponseEntity<ApiResponse<AdminNoticeDetailDto>> getNoticeDetail(@PathVariable Long noticeId) {
        return ResponseEntity.ok(ApiResponse.success("공지 상세 조회 성공", noticeService.getNoticeDetail(noticeId)));
    }

}
