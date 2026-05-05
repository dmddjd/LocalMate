package com.localmate.api.admin.statistics.controller;

import com.localmate.api.admin.statistics.dto.StatisticsDto;
import com.localmate.api.admin.statistics.service.AdminStatisticsService;
import com.localmate.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/statistics")
@Tag(name = "Admin - Statistics Controller", description = "관리자 - 통계 API 입니다.")
public class AdminStatisticsController {
    private final AdminStatisticsService statisticsService;

    @GetMapping("/signup")
    @Operation(summary = "회원가입 통계", description = "일/주/월 단위 회원가입 통계를 조회합니다.")
    public ResponseEntity<ApiResponse<List<StatisticsDto>>> getSignupStats(@RequestParam(defaultValue = "day") String unit) {
        return ResponseEntity.ok(ApiResponse.success("회원가입 통계 조회 성공", statisticsService.getSignupStats(unit)));
    }

    @GetMapping("/withdraw")
    @Operation(summary = "회원탈퇴 통계", description = "일/주/월 단위 회원탈퇴 통계를 조회합니다.")
    public ResponseEntity<ApiResponse<List<StatisticsDto>>> getWithdrawStats(@RequestParam(defaultValue = "day") String unit) {
        return ResponseEntity.ok(ApiResponse.success("회원탈퇴 통계 조회 성공", statisticsService.getWithdrawStats(unit)));
    }

    @GetMapping("/activeuser")
    @Operation(summary = "활성 사용자 통계", description = "dau/wau/mau 단위 활성 사용자 통계를 조회합니다.")
    public ResponseEntity<ApiResponse<Long>> getActiveUserStats(@RequestParam(defaultValue = "day") String unit) {
        return ResponseEntity.ok(ApiResponse.success("활성 사용자 통계 조회 성공", statisticsService.getActiveUserStats(unit)));
    }

    @GetMapping("/report")
    @Operation(summary = "신고 접수 통계", description = "일/주/월 단위 신고 접수 통계를 조회합니다.")
    public ResponseEntity<ApiResponse<List<StatisticsDto>>> getReportStats(@RequestParam(defaultValue = "day") String unit) {
        return ResponseEntity.ok(ApiResponse.success("신고 접수 통계 조회 성공", statisticsService.getReportStats(unit)));
    }

    @GetMapping("/sanction")
    @Operation(summary = "제재 처리 통계", description = "일/주/월 단위 제재 처리 통계를 조회합니다.")
    public ResponseEntity<ApiResponse<List<StatisticsDto>>> getSanctionStats(@RequestParam(defaultValue = "day") String unit) {
        return ResponseEntity.ok(ApiResponse.success("제재 처리 통계 조회 성공", statisticsService.getSanctionStats(unit)));
    }

    @GetMapping("/chatroom")
    @Operation(summary = "채팅방 생성 통계", description = "일/주/월 단위 채팅방 생성 통계를 조회합니다.")
    public ResponseEntity<ApiResponse<List<StatisticsDto>>> getChatRoomStats(@RequestParam(defaultValue = "day") String unit) {
        return ResponseEntity.ok(ApiResponse.success("채팅방 생성 통계 조회 성공", statisticsService.getChatRoomStats(unit)));
    }

    @GetMapping("/chatmsg")
    @Operation(summary = "채팅 메세지 발송 통계", description = "일/주/월 단위 채팅 메세지 발송 통계를 조회합니다.")
    public ResponseEntity<ApiResponse<List<StatisticsDto>>> getChatMsgStats(@RequestParam(defaultValue = "day") String unit) {
        return ResponseEntity.ok(ApiResponse.success("채팅 메세지 발송 통계 조회 성공", statisticsService.getChatMsgStats(unit)));
    }
}
