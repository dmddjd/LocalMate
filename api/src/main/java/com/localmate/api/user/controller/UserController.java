package com.localmate.api.user.controller;

import com.localmate.api.global.response.ApiResponse;
import com.localmate.api.user.dto.FindUserDto;
import com.localmate.api.user.dto.ProfileDto;
import com.localmate.api.user.dto.ProfileUpdateDto;
import com.localmate.api.user.dto.UserSearchDto;
import com.localmate.api.user.dto.UserUpdateDto;
import com.localmate.api.user.service.UserService;
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
@RequestMapping("/user")
@Tag(name = "User Controller", description = "유저 API 입니다.")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile/{userId}")
    @Operation(summary = "프로필 조회", description = "userId로 프로필을 조회합니다.")
    public ResponseEntity<ApiResponse<ProfileDto>> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("프로필 조회 성공!", userService.getProfile(userId)));
    }

    @PatchMapping("/info")
    @Operation(summary = "계정 정보 수정", description = "로그인한 유저의 계정 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<Void>> updateUserInfo(
            @AuthenticationPrincipal Long userId,
            @RequestBody UserUpdateDto userUpdateDto) {
        userService.updateUserInfo(userId, userUpdateDto);
        return ResponseEntity.ok(ApiResponse.success("계정 정보 수정 성공!", null));
    }

    @PatchMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로필 정보 수정", description = "로그인한 유저의 프로필 정보를 수정합니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                encoding = @Encoding(name = "data",
                    contentType = "application/json")))
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @AuthenticationPrincipal Long userId,
            @RequestPart("data") ProfileUpdateDto profileUpdateDto,
            @RequestPart(required = false) MultipartFile profileImage) {
        userService.updateProfile(userId, profileUpdateDto, profileImage);
        return ResponseEntity.ok(ApiResponse.success("프로필 수정 성공!", null));
    }

    @GetMapping("/users")
    @Operation(summary = "현지인 목록 조회", description = "여행지의 현지인 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<FindUserDto>>> findUsers(UserSearchDto userSearchDto) {
        return ResponseEntity.ok(ApiResponse.success("현지인 목록 조회 성공!", userService.findUsers(userSearchDto)));
    }

    @PostMapping("/recommend/{toUserId}")
    @Operation(summary = "유저 추천/추천 취소", description = "유저를 추천/추천 취소 합니다.")
    public ResponseEntity<ApiResponse<Void>> recommendation(
            @PathVariable Long toUserId,
            @AuthenticationPrincipal Long userId
    ) {
        userService.recommendation(userId, toUserId);
        return ResponseEntity.ok(ApiResponse.success("추천 처리 완료", null));
    }

}