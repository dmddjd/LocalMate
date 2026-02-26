package com.localmate.api.global.security;

import com.localmate.api.global.jwt.JwtFilter;
import com.localmate.api.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) // CORS 설정
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // 기본 로그인 페이지 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // 인증 팝업 비활성화

                // Jwt 기반 인증이므로 세션을 사용하지 않음
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 엔드포인트별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/index.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // 회원가입, 로그인 페이지 경로는 모든 접근 허용
                        .requestMatchers("/admin").hasAuthority("ADMIN") //  관리자 페이지 경로는 ADMIN 권한 필요
                        .anyRequest().authenticated() // 그 외의 요청은 인증된 사용자만 접근 가능
                )

                // UsernamePasswordAuthenticationFilter 실행 이전에 JwtFilter 실행
                .addFilterBefore(new JwtFilter(jwtUtil, customUserDetailsService), UsernamePasswordAuthenticationFilter.class)

                // OAuth2
                .oauth2Login(oauth2 -> oauth2.defaultSuccessUrl("/index.html", true));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
