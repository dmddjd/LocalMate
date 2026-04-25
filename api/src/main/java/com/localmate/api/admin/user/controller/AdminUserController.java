package com.localmate.api.admin.user.controller;

import com.localmate.api.admin.user.dto.AdminUserChangeStatusDto;
import com.localmate.api.admin.user.dto.AdminUserDetailDto;
import com.localmate.api.admin.user.dto.AdminUserListDto;
import com.localmate.api.admin.user.dto.AdminUserChangeRoleDto;
import com.localmate.api.admin.user.service.AdminUserService;
import com.localmate.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/user")
@Tag(name = "Admin - User Controller", description = "관리자 - 유저 API 입니다.")
public class AdminUserController {
    private final AdminUserService adminUserService;

    @GetMapping
    @Operation(summary = "모든 유저 조회", description = "모든 유저를 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<AdminUserListDto>>> getAllUser() {
        return ResponseEntity.ok(ApiResponse.success("모든 유저 목록 조회 성공", adminUserService.getAllUser()));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "유저 상세 조회", description = "유저 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<AdminUserDetailDto>> getUserDetail(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("유저 상세 정보 조회 성공", adminUserService.getUserDtail(userId)));
    }

    @PatchMapping("/{userId}/role")
    @Operation(summary = "유저 권한 변경", description = "유저의 권한을 변경합니다.")
    public ResponseEntity<ApiResponse<Void>> changeRole(
            @PathVariable Long userId,
            @RequestBody AdminUserChangeRoleDto dto
            ) {
        adminUserService.changeRole(userId, dto.getRole());
        return ResponseEntity.ok(ApiResponse.success("권한 변경 성공", null));
    }

    @PatchMapping("/{userId}/status")
    @Operation(summary = "유저 상태 변경", description = "유저의 상태를 변경합니다.")
    public ResponseEntity<ApiResponse<Void>> changeStatus(
            @PathVariable Long userId,
            @RequestBody AdminUserChangeStatusDto dto
            ) {
        adminUserService.changeStatus(userId, dto.getStatus());
        return ResponseEntity.ok(ApiResponse.success("상태 변경 성공", null));
    }


}
