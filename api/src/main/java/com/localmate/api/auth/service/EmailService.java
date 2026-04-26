package com.localmate.api.auth.service;

import com.localmate.api.global.exception.CustomException;
import com.localmate.api.global.redis.RedisUtil;
import com.localmate.api.user.domain.UserStatus;
import com.localmate.api.user.domain.User;
import com.localmate.api.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${mail.verification.expiration}")
    private long expiration;

    private static final String SIGNUP_CODE_PREFIX = "SIGNUP_CODE : " ;
    private static final String SIGNUP_VERIFIED_PREFIX = "SIGNUP_VERIFIED : ";

    private static final String RESTORE_CODE_PREFIX = "RESTORE_CODE : ";
    private static final String RESTORE_VERIFIED_PREFIX = "RESTORE_VERIFIED : ";

    private static final String FIND_ID_CODE_PREFIX = "FIND_ID_CODE : ";
    private static final String FIND_ID_VERIFIED_PREFIX = "FIND_ID_VERIFIED : ";

    private static final String PW_RESET_CODE_PREFIX = "PW_RESET_CODE : ";
    private static final String PW_RESET_VERIFIED_PREFIX = "PW_RESET_VERIFIED : ";

    // 6자리 랜덤 숫자 생성
    private String generateCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    // 회원가입 인증번호 발송
    public void sendSignupCode(String email) {
        String code = generateCode();
        redisUtil.setDataExpire(SIGNUP_CODE_PREFIX + email, code, expiration);

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("[LocalMate] 회원가입 인증번호");
            helper.setText("인증번호는 <b>" + code +"</b> 입니다. 5분 내에 입력해주세요.", true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "메일 발송 중 오류가 발생했습니다.");
        }
    }

    // 회원가입 인증번호 검증
    public void verifySignupCode(String email, String code) {
        String savedCode = redisUtil.getData(SIGNUP_CODE_PREFIX + email);
        if(savedCode == null || !code.equals(savedCode)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않거나, 만료되었습니다.");
        }
        redisUtil.deleteData(SIGNUP_CODE_PREFIX + email);
        redisUtil.setDataExpire(SIGNUP_VERIFIED_PREFIX + email, "true", 600);
    }

    // 회원가입 인증 완료 여부 확인
    public boolean isSignupVerified(String email) {
        return "true".equals(redisUtil.getData(SIGNUP_VERIFIED_PREFIX + email));
    }

    // 회원가입 인증 완료 후 삭제
    public void removeSignupVerified(String email) {
        redisUtil.deleteData(SIGNUP_VERIFIED_PREFIX + email);
    }

    // 계정 복구 인증번호 발송
    public String sendRestoreCode(String id, String password) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 아이디입니다."));

        if (user.getStatus() != UserStatus.DELETED) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "탈퇴한 계정이 아닙니다.");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
        }

        String code = generateCode();
        redisUtil.setDataExpire(RESTORE_CODE_PREFIX +user.getEmail(), code, expiration);

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject("[LocalMate] 계정 복구 인증번호");
            helper.setText("인증번호는 <b>" + code + "</b> 입니다. 5분 내에 입력해주세요.", true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "메일 발송 중 오류가 발생했습니다.");
        }

        return maskEmail(user.getEmail());
    }

    private String maskEmail(String email) {
        int index = email.indexOf("@");
        String origin = email.substring(0, index);
        String masked = origin.substring(0, Math.min(2, origin.length())) + "***";
        return masked + email.substring(index);
    }

    // 계정 복구 인증번호 검증
    public void verifyRestoreCode(String email, String code) {
        String savedCode = redisUtil.getData(RESTORE_CODE_PREFIX + email);
        if(savedCode == null || !code.equals(savedCode)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않거나, 만료되었습니다.");
        }
        redisUtil.deleteData(RESTORE_CODE_PREFIX + email);
        redisUtil.setDataExpire(RESTORE_VERIFIED_PREFIX + email, "true", 600);
    }

    // 계정 복구 인증 완료 여부 확인
    public boolean isRestoreVerified(String email) {
        return "true".equals(redisUtil.getData(RESTORE_VERIFIED_PREFIX + email));
    }

    // 계정 복구 인증 완료 후 삭제
    public void removeRestoreVerified(String email) {
        redisUtil.deleteData(RESTORE_VERIFIED_PREFIX + email);
    }

    // 아이디 찾기 인증번호 발송
    public void sendFindIdCode(String userName, String email) {
        userRepository.findByUserNameAndEmail(userName, email).orElseThrow(() ->
                new CustomException(HttpStatus.NOT_FOUND, "이름과 이메일이 일치하는 사용자를 찾을 수 없습니다."));

        String code = generateCode();
        redisUtil.setDataExpire(FIND_ID_CODE_PREFIX + email, code, expiration);

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("[LocalMate] 아이디 찾기 인증번호");
            helper.setText("인증번호는 <b>" + code + "</b> 입니다. 5분 내에 입력해주세요.", true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "메일 발송 중 오류가 발생했습니다.");
        }
    }

    //아이디 찾기 인증번호 검증
    public void verifyFindIdCode(String email, String code) {
        String savedCode = redisUtil.getData(FIND_ID_CODE_PREFIX + email);
        if (savedCode == null || !code.equals(savedCode)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않거나, 만료되었습니다.");
        }
        redisUtil.deleteData(FIND_ID_CODE_PREFIX + email);
        redisUtil.setDataExpire(FIND_ID_VERIFIED_PREFIX + email, "true", 600);
    }

    // 아이디 찾기 인증 완료 여부 확인
    public boolean isFindIdVerified(String email) {
        return "true".equals(redisUtil.getData(FIND_ID_VERIFIED_PREFIX + email));
    }

    // 아이디 찾기 인증 완료 후 삭제
    public void removeFindIdVerified(String email) {
        redisUtil.deleteData(FIND_ID_VERIFIED_PREFIX + email);
    }

    // 비밀번호 재설정 인증번호 발송
    public void sendPasswordResetCode(String id, String email) {
        userRepository.findByIdAndEmail(id, email).orElseThrow(() ->
                new CustomException(HttpStatus.NOT_FOUND, "아이디와 이메일이 일치하는 사용자를 찾을 수 없습니다."));

        String code = generateCode();
        redisUtil.setDataExpire(PW_RESET_CODE_PREFIX + email, code, expiration);

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("[LocalMate] 비밀번호 재설정 인증번호");
            helper.setText("인증번호는 <b>" + code + "</b> 입니다. 5분 내에 입력해주세요.", true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "메일 발송 중 오류가 발생했습니다.");
        }
    }

    // 비밀번호 재설정 인증번호 검증
    public void verifyPasswordResetCode(String email, String code) {
        String savedCode = redisUtil.getData(PW_RESET_CODE_PREFIX + email);
        if(savedCode == null || !code.equals(savedCode)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않거나, 만료되었습니다.");
        }
        redisUtil.deleteData(PW_RESET_CODE_PREFIX + email);
        redisUtil.setDataExpire(PW_RESET_VERIFIED_PREFIX + email, "true", 600);
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
