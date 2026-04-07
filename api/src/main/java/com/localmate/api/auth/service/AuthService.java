package com.localmate.api.auth.service;

import com.localmate.api.auth.dto.*;
import com.localmate.api.global.exception.CustomException;
import com.localmate.api.global.jwt.JwtUtil;
import com.localmate.api.global.redis.RedisUtil;
import com.localmate.api.user.domain.Profile;
import com.localmate.api.user.domain.Status;
import com.localmate.api.user.domain.User;
import com.localmate.api.user.repository.ProfileRepository;
import com.localmate.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    public void signup(SignupDto signupDto) {
        if (!emailService.isSignupVerified(signupDto.getEmail())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "이메일 인증이 완료되지 않았습니다.");
        }

        if (userRepository.findById(signupDto.getId()).isPresent()) throw new CustomException(HttpStatus.BAD_REQUEST, "아이디 중복");
        if (userRepository.findByEmail(signupDto.getEmail()).isPresent()) throw new CustomException(HttpStatus.BAD_REQUEST, "이메일 중복");
        if (userRepository.findByPhoneNumber(signupDto.getPhoneNumber()).isPresent()) throw new CustomException(HttpStatus.BAD_REQUEST, "전화번호 중복");
        if (userRepository.findByNickname(signupDto.getNickname()).isPresent()) throw new CustomException(HttpStatus.BAD_REQUEST, "닉네임 중복");

        User user = userRepository.save(createUserEntity(signupDto));
        profileRepository.save(new Profile(user));
        emailService.removeSignupVerified(signupDto.getEmail());
    }

    private User createUserEntity(SignupDto signupDto) {
        return User.builder()
                .userName(signupDto.getUserName())
                .nickname(signupDto.getNickname())
                .id(signupDto.getId())
                .password(passwordEncoder.encode(signupDto.getPassword()))
                .email(signupDto.getEmail())
                .birthDate(signupDto.getBirthDate())
                .gender(signupDto.getGender())
                .phoneNumber(signupDto.getPhoneNumber())
                .country(signupDto.getCountry())
                .city(signupDto.getCity())
                .addressLine1(signupDto.getAddressLine1())
                .addressLine2(signupDto.getAddressLine2())
                .build();
    }

    public void withdraw(Long userId, String password) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다."));

        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
        }

        user.withdraw();
    }

    public void restore(RestoreAccountDto restoreAccountDto) {
        if (!emailService.isRestoreVerified(restoreAccountDto.getEmail())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "이메일 인증이 완료되지 않았습니다.");
        }

        User user = userRepository.findById(restoreAccountDto.getId()).orElseThrow(() ->
                new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 아이디 입니다."));

        if (user.getStatus() != Status.DELETE) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "탈퇴한 계정이 아닙니다.");
        }

        user.restore();
        emailService.removeRestoreVerified(restoreAccountDto.getEmail());
    }

    public Map<String, String> login(LoginDto loginDto) {
        User user = userRepository.findById(loginDto.getId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 아이디입니다."));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
        }

        if (user.getStatus() == Status.DELETE) {
            throw new CustomException(HttpStatus.FORBIDDEN, "탈퇴한 계정입니다. 30일 이내로 복구 가능합니다.");
        }

        String accessToken = jwtUtil.createAccessToken(user.getUserId(), user.getRole().name());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());

        redisUtil.setDataExpire(user.getUserId().toString(), refreshToken, 60 * 60 * 24 * 7L);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken);
        tokenMap.put("refreshToken", refreshToken);

        return tokenMap;
    }

    public void logout(Long userId, String accessToken) {
        redisUtil.deleteData(userId.toString());

        Long expiration = jwtUtil.getExpiration(accessToken);
        redisUtil.setDataExpire("BlackList:" + accessToken, "logout", expiration);
    }

    public String findId(FindIdDto findIdDto) {
        if (!emailService.isFindIdVerified(findIdDto.getEmail())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "이메일 인증이 완료되지 않았습니다.");
        }

        User user = userRepository.findByUserNameAndEmail(findIdDto.getUserName(), findIdDto.getEmail())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "일치하는 사용자를 찾을 수 없습니다."));

        emailService.removeFindIdVerified(findIdDto.getEmail());
        return user.getId();
    }

    public void resetPassword(ResetPasswordDto resetPasswordDto) {
        if (!emailService.isPasswordResetVerified(resetPasswordDto.getEmail())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "이메일 인증이 완료되지 않았습니다.");
        }

        User user = userRepository.findByEmail(resetPasswordDto.getEmail())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));

        user.updatePassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
        emailService.removePasswordResetVerified(resetPasswordDto.getEmail());
    }

    public Map<String, String> reissue(String refreshToken) {
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh Token입니다.");
        }

        Long userId = jwtUtil.getUserId(refreshToken);

        String savedToken = redisUtil.getData(userId.toString());
        if (savedToken == null || !savedToken.equals(refreshToken)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "Refresh Token이 일치하지 않습니다.");
        }

        User user = userRepository.findByUserId(userId).orElseThrow(() ->
                new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        String newAccessToken = jwtUtil.createAccessToken(userId, user.getRole().name());
        String newRefreshToken = jwtUtil.createRefreshToken(userId);

        redisUtil.setDataExpire(userId.toString(), newRefreshToken, 60 * 60 * 24 * 7L);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", newAccessToken);
        tokenMap.put("refreshToken", newRefreshToken);

        return tokenMap;
    }
}
