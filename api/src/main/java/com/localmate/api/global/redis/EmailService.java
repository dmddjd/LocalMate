package com.localmate.api.global.redis;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;

    @Value("${mail.verification.expiration}")
    private long expiration;

    // 6자리 랜덤 숫자 생성
    private String generateCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    // 메일 발송
    public void sendCode(String email) throws MessagingException {
        // 인증번호 생성
        String verificationCode = generateCode();

        // Redis에 저장
        redisUtil.setDataExpire(email, verificationCode, expiration);

        // 메일 전송
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject("[LocalMate] 회원가입 인증번호");
        helper.setText("인증번호는 <b>" + verificationCode + "</b>입니다. 5분 내에 입력해주세요.", true);

        javaMailSender.send(message);
    }

    // 인증번호 검증
    public boolean verifyCode(String email, String code){
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
        String verified = redisUtil.getData("Email_Verified : " + email);

        return "true".equals(verified);
    }

    // 인증 완료 후 삭제
    public void removeVerified(String email) {
        redisUtil.deleteData("Email_Verified : " + email);
    }
}
