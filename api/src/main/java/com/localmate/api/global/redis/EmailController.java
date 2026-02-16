package com.localmate.api.global.redis;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
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
    @Operation(summary = "인증번호 발송", description = "입력한 이메일로 6자리 인증번호를 발송합니다.")
    public ResponseEntity<String> sendEmail(@RequestParam String email) {
        try {
            emailService.sendCode(email);
            return ResponseEntity.ok("인증번호가 발송되었습니다.");
        } catch (MessagingException e) {
            return ResponseEntity.internalServerError().body("메일 발송 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/email/verify")
    @Operation(summary = "인증번호 검증", description = "입력한 인증번호가 맞는지 검증합니다.")
    public ResponseEntity<String> verifyCode(@RequestParam String email, @RequestParam String code) {
        boolean isVerified = emailService.verifyCode(email, code);

        if (isVerified) {
            return ResponseEntity.ok("인증 성공");
        } else {
            return ResponseEntity.badRequest().body("인증번호가 일치하지 않거나, 만료되었습니다.");
        }
    }


}
