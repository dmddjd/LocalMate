package com.localmate.api.admin.notice.controller;

import com.localmate.api.admin.notice.dto.AdminCreateNoticeDto;
import com.localmate.api.admin.notice.dto.AdminEditNoticeDto;
import com.localmate.api.admin.notice.dto.AdminNoticeListDto;
import com.localmate.api.admin.notice.service.AdminNoticeService;
import com.localmate.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/notice")
@Tag(name = "Admin - Notice Controller", description = "관리자 - 공지 API 입니다.")
public class AdminNoticeController {
    private final AdminNoticeService adminNoticeService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "공지 등록", description = "공지를 등록합니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    encoding = @Encoding(name = "data",
                            contentType = "application/json")))
    public ResponseEntity<ApiResponse<Void>> createNotice(
            @RequestPart("data") AdminCreateNoticeDto request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal Long userId
            ) {
        adminNoticeService.createNotice(userId, request, files);
        return ResponseEntity.ok(ApiResponse.success("공지 등록 성공", null));
    }

    @GetMapping
    @Operation(summary = "공지 목록 조회", description = "공지 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<AdminNoticeListDto>>> getAllNotice() {
        return ResponseEntity.ok(ApiResponse.success("공지 목록 조회 성공", adminNoticeService.getAllNotice()));
    }

    @PatchMapping(value = "/{noticeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "공지 수정", description = "공지를 수정합니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    encoding = @Encoding(name = "data",
                            contentType = "application/json")))
    public ResponseEntity<ApiResponse<Void>> editNotice(
            @PathVariable Long noticeId,
            @RequestPart("data")AdminEditNoticeDto request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
            ) {
        adminNoticeService.editNotice(noticeId, request, files);
        return ResponseEntity.ok(ApiResponse.success("공지 수정 성공", null));
    }

    @DeleteMapping("/{noticeId}")
    @Operation(summary = "공지 삭제", description = "공지를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteNotice(@PathVariable Long noticeId) {
        adminNoticeService.deleteNotice(noticeId);
        return ResponseEntity.ok(ApiResponse.success("공지 삭제 성공", null));
    }
}
