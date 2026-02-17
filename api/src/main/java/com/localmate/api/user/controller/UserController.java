package com.localmate.api.user.controller;

import com.localmate.api.global.ApiResponse;
import com.localmate.api.user.dto.LoginDto;
import com.localmate.api.user.dto.SignupDto;
import com.localmate.api.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "User Controller", description = "유저 API 입니다.")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "입력값을 기반으로 회원가입합니다.")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupDto signupDto) {
        userService.signup(signupDto);
        return ResponseEntity.ok(ApiResponse.success("회원가입 성공!", null));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인합니다.")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(@Valid @RequestBody LoginDto loginDto) {
        Map<String, String> tokenMap = userService.login(loginDto);
        return ResponseEntity.ok(ApiResponse.success("로그인 성공!", tokenMap));
    }
}
