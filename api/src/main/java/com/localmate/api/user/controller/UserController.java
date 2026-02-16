package com.localmate.api.user.controller;

import com.localmate.api.exception.CustomException;
import com.localmate.api.global.ApiResponse;
import com.localmate.api.user.domain.User;
import com.localmate.api.user.dto.LoginDto;
import com.localmate.api.user.dto.SignupDto;
import com.localmate.api.user.service.UserService;
import com.localmate.api.security.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "User Controller", description = "멤버 컨트롤러 입니다.")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "회원가입 API 입니다.")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupDto signupDto) {
        userService.signup(signupDto);
        return ResponseEntity.ok(ApiResponse.success("회원가입 성공!", null));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인 API 입니다.")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {

        User user = userService.findById(loginDto.getId())
                .orElseThrow(() -> new CustomException("존재하지 않는 아이디입니다."));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new CustomException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtUtil.createRefreshToken(user.getId());

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken);
        tokenMap.put("refreshToken", refreshToken);

        return ResponseEntity.ok(ApiResponse.success("로그인 성공!", tokenMap));
    }
}
