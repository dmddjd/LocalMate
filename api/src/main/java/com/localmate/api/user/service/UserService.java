package com.localmate.api.user.service;


import com.localmate.api.global.exception.CustomException;
import com.localmate.api.global.redis.EmailService;
import com.localmate.api.security.jwt.JwtUtil;
import com.localmate.api.user.domain.User;
import com.localmate.api.user.dto.LoginDto;
import com.localmate.api.user.dto.SignupDto;
import com.localmate.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public void signup(SignupDto signupDto) {
        if(!emailService.isVerified(signupDto.getEmail())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "이메일 인증이 완료되지 않았습니다.");
        }

        if(userRepository.findById(signupDto.getId()).isPresent()) throw new CustomException(HttpStatus.BAD_REQUEST, "아이디 중복");
        if(userRepository.findByEmail(signupDto.getEmail()).isPresent()) throw new CustomException(HttpStatus.BAD_REQUEST, "이메일 중복");
        if(userRepository.findByPhoneNumber(signupDto.getPhoneNumber()).isPresent()) throw new CustomException(HttpStatus.BAD_REQUEST, "전화번호 중복");

        User user = createUserEntity(signupDto);

        userRepository.save(user);

        emailService.removeVerified(signupDto.getEmail());

        log.info(signupDto.getId());
        log.info(signupDto.getPassword());
    }

    private User createUserEntity(SignupDto signupDto) {
        return User.builder()
                .userName(signupDto.getUserName())
                .id(signupDto.getId())
                .password(passwordEncoder.encode(signupDto.getPassword()))
                .email(signupDto.getEmail())
                .birthDate(signupDto.getBirthDate())
                .gender(signupDto.getGender())
                .phoneNumber(signupDto.getPhoneNumber())
                .countryCode(signupDto.getCountryCode())
                .city(signupDto.getCity())
                .addressLine1(signupDto.getAddressLine1())
                .addressLine2(signupDto.getAddressLine2())
                .build();
    }

    public Map<String, String> login(LoginDto loginDto) {
        User user = userRepository.findById(loginDto.getId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 아이디입니다."));

        if(!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtUtil.createRefreshToken(user.getId());

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken);
        tokenMap.put("refreshToken", refreshToken);

        return tokenMap;
    }
}
