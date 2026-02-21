package com.localmate.api.auth.controller;

import com.localmate.api.global.response.ApiResponse;
import com.localmate.api.auth.dto.FindIdDto;
import com.localmate.api.auth.dto.LoginDto;
import com.localmate.api.auth.dto.ResetPasswordDto;
import com.localmate.api.auth.dto.SignupDto;
import com.localmate.api.auth.service.AuthService;
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
@Tag(name = "Auth Controller", description = "인증 API 입니다.")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "입력값을 기반으로 회원가입합니다.")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupDto signupDto) {
        authService.signup(signupDto);
        return ResponseEntity.ok(ApiResponse.success("회원가입 성공!", null));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인합니다.")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(@Valid @RequestBody LoginDto loginDto) {
        Map<String, String> tokenMap = authService.login(loginDto);
        return ResponseEntity.ok(ApiResponse.success("로그인 성공!", tokenMap));
    }

    @PostMapping("/find-id")
    @Operation(summary = "아이디 찾기", description = "이름과 이메일로 아이디를 찾습니다.")
    public ResponseEntity<ApiResponse<String>> findId(@RequestBody FindIdDto findIdDto) {
        String id = authService.findId(findIdDto);
        return ResponseEntity.ok(ApiResponse.success("아이디 찾기 성공!", id));
    }

    @PostMapping("/find-password")
    @Operation(summary = "비밀번호 재설정", description = "이메일 인증 완료 후 새 비밀번호로 재설정합니다.")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        authService.resetPassword(resetPasswordDto);
        return ResponseEntity.ok(ApiResponse.success("비밀번호가 재설정되었습니다.", null));
    }
}
