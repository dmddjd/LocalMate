package com.localmate.api.auth.controller;

import com.localmate.api.auth.service.EmailService;
import com.localmate.api.global.exception.CustomException;
import com.localmate.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Email Controller", description = "이메일 인증 API 입니다.")
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/email/send")
    @Operation(summary = "회원가입 인증번호 발송", description = "입력한 이메일로 6자리 인증번호를 발송합니다.")
    public ResponseEntity<ApiResponse<Void>> sendEmail(@RequestParam String email) {
        emailService.sendCode(email);
        return ResponseEntity.ok(ApiResponse.success("인증번호가 발송되었습니다.", null));
    }

    @PostMapping("/email/verify")
    @Operation(summary = "회원가입 인증번호 검증", description = "입력한 인증번호가 맞는지 검증합니다.")
    public ResponseEntity<ApiResponse<Void>> verifyCode(@RequestParam String email, @RequestParam String code) {
        emailService.verifyCode(email, code);
        return ResponseEntity.ok(ApiResponse.success("인증 성공", null));
    }

    @PostMapping("/email/find-password/send")
    @Operation(summary = "비밀번호 재설정 인증번호 발송", description = "아이디와 이메일로 본인 확인 후 인증번호를 발송합니다.")
    public ResponseEntity<ApiResponse<Void>> sendPasswordResetCode(@RequestParam String id, @RequestParam String email) {
        emailService.sendPasswordResetCode(id, email);
        return ResponseEntity.ok(ApiResponse.success("인증번호가 발송되었습니다.", null));
    }

    @PostMapping("/email/find-password/verify")
    @Operation(summary = "비밀번호 재설정 인증번호 검증", description = "인증번호를 검증합니다.")
    public ResponseEntity<ApiResponse<Void>> verifyPasswordResetCode(@RequestParam String email, @RequestParam String code) {
        emailService.verifyPasswordResetCode(email, code);
        return ResponseEntity.ok(ApiResponse.success("인증 성공", null));
    }
}
