package com.localmate.api.auth.service;

import com.localmate.api.global.exception.CustomException;
import com.localmate.api.global.redis.RedisUtil;
import com.localmate.api.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;

    @Value("${mail.verification.expiration}")
    private long expiration;

    private static final String PW_RESET_CODE_PREFIX = "PW_RESET_CODE : ";
    private static final String PW_RESET_VERIFIED_PREFIX = "PW_RESET_VERIFIED : ";
    private static final long PW_RESET_VERIFIED_TTL = 600;

    // 6자리 랜덤 숫자 생성
    private String generateCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    // ===================== 회원가입 이메일 인증 =====================

    // 인증번호 발송
    public void sendCode(String email) throws MessagingException {
        String verificationCode = generateCode();

        redisUtil.setDataExpire(email, verificationCode, expiration);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject("[LocalMate] 회원가입 인증번호");
        helper.setText("인증번호는 <b>" + verificationCode + "</b>입니다. 5분 내에 입력해주세요.", true);

        javaMailSender.send(message);
    }

    // 인증번호 검증
    public boolean verifyCode(String email, String code) {
        String savedCode = redisUtil.getData(email);
        if (savedCode == null) return false;

        if (code.equals(savedCode)) {
            redisUtil.deleteData(email);
            redisUtil.setDataExpire("Email_Verified : " + email, "true", 600);
            return true;
        }

        return false;
    }

    // 인증 완료 여부 확인
    public boolean isVerified(String email) {
        return "true".equals(redisUtil.getData("Email_Verified : " + email));
    }

    // 인증 완료 후 삭제
    public void removeVerified(String email) {
        redisUtil.deleteData("Email_Verified : " + email);
    }

    // ===================== 비밀번호 재설정 이메일 인증 =====================

    // 비밀번호 재설정 인증번호 발송 (사용자 존재 여부 검증 포함)
    public void sendPasswordResetCode(String id, String email) throws MessagingException {
        userRepository.findByIdAndEmail(id, email)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "아이디와 이메일이 일치하는 사용자를 찾을 수 없습니다."));

        String code = generateCode();
        redisUtil.setDataExpire(PW_RESET_CODE_PREFIX + email, code, expiration);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject("[LocalMate] 비밀번호 재설정 인증번호");
        helper.setText("인증번호는 <b>" + code + "</b>입니다. 5분 내에 입력해주세요.", true);

        javaMailSender.send(message);
    }

    // 비밀번호 재설정 인증번호 검증
    public boolean verifyPasswordResetCode(String email, String code) {
        String savedCode = redisUtil.getData(PW_RESET_CODE_PREFIX + email);
        if (savedCode == null) return false;

        if (!code.equals(savedCode)) return false;

        redisUtil.deleteData(PW_RESET_CODE_PREFIX + email);
        redisUtil.setDataExpire(PW_RESET_VERIFIED_PREFIX + email, "true", PW_RESET_VERIFIED_TTL);
        return true;
    }

    // 비밀번호 재설정 인증 완료 여부 확인
    public boolean isPasswordResetVerified(String email) {
        return "true".equals(redisUtil.getData(PW_RESET_VERIFIED_PREFIX + email));
    }

    // 비밀번호 재설정 인증 완료 후 삭제
    public void removePasswordResetVerified(String email) {
        redisUtil.deleteData(PW_RESET_VERIFIED_PREFIX + email);
    }
}
