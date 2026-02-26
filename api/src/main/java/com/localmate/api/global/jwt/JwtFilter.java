package com.localmate.api.global.jwt;

import com.localmate.api.global.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Authorization 헤더 가져오기
        String authorization = request.getHeader("Authorization");

        // 2. Authorization 헤더가 없거나 Bearer로 시작하지 않으면 통과
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 토큰만 추출 (Bearer 제거)
        String token = authorization.substring(7);

        // 4. Access Token 검증
        if (!jwtUtil.validateAccessToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 5. id 추출 후 CustomUserDetails 로드
        String id = jwtUtil.getId(token);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(id);

        // 6. SecurityContext에 Authentication 등록
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 7. 다음 필터로 넘김
        filterChain.doFilter(request, response);
    }
}
