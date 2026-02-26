package com.localmate.api.user.controller;

import com.localmate.api.global.response.ApiResponse;
import com.localmate.api.global.security.CustomUserDetails;
import com.localmate.api.user.dto.ProfileDto;
import com.localmate.api.user.dto.ProfileUpdateDto;
import com.localmate.api.user.dto.UserUpdateDto;
import com.localmate.api.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "User Controller", description = "유저 API 입니다.")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile/{userId}")
    @Operation(summary = "프로필 조회", description = "userId로 프로필을 조회합니다.")
    public ResponseEntity<ApiResponse<ProfileDto>> getProfile(@PathVariable Long userId) {
        ProfileDto response = userService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("프로필 조회 성공!", response));
    }

    @PatchMapping("/info")
    @Operation(summary = "계정 정보 수정", description = "로그인한 유저의 계정 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<Void>> updateUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserUpdateDto userUpdateDto) {
        userService.updateUserInfo(userDetails.getUser().getUserId(), userUpdateDto);
        return ResponseEntity.ok(ApiResponse.success("계정 정보 수정 성공!", null));
    }

    @PatchMapping("/profile")
    @Operation(summary = "프로필 정보 수정", description = "로그인한 유저의 프로필 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ProfileUpdateDto profileUpdateDto) {
        userService.updateProfile(userDetails.getUser().getUserId(), profileUpdateDto);
        return ResponseEntity.ok(ApiResponse.success("프로필 수정 성공!", null));
    }
}
