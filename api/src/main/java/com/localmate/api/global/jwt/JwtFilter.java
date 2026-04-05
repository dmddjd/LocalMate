package com.localmate.api.global.jwt;

import com.localmate.api.global.redis.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

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

        // 5. 블랙리스트 체크
        if (redisUtil.getData("BlackList:" + token) != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 6. id, role 추출
        Long userId = jwtUtil.getUserId(token);
        String role = jwtUtil.getRole(token);

        // 7. SecurityContext에 Authentication 등록
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userId, null, List.of(new SimpleGrantedAuthority(role)));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 8. 다음 필터로 넘김
        filterChain.doFilter(request, response);
    }
}
