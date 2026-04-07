package com.localmate.api.global.chat;

import com.localmate.api.global.exception.CustomException;
import com.localmate.api.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
// ChannelInterceptor : WebSocket 연결 시 JWT 토큰을 검증하고 인증 정보를 설정함
public class StompHandler implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    // 클라이언트가 메세지를 보내기 전에 가로챔
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // 처음 연결 시(CONNECT)에만 실행
        if(StompCommand.CONNECT.equals(accessor.getCommand())) {

            // Authorization 헤더 추출
            String token = accessor.getFirstNativeHeader("Authorization");

            // 토큰이 없거나 Bearer로 시작하지 않으면 연결 차단
            if (token == null || !token.startsWith("Bearer ")) {
                throw new MessageDeliveryException("Authorization 헤더가 없습니다.");
            }

            // 토큰만 추출
            token = token.substring(7);

            // 토큰 유효성 검증 실패 시 연결 차단
            if (!jwtUtil.validateAccessToken(token)) {
                throw new MessageDeliveryException("유효하지 않은 토큰입니다.");
            }

            // 토큰에서 userId 추출
            Long userId = jwtUtil.getUserId(token);

            // 인증 객체 생성 후 WebSocket 세션에 저장
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userId, null, List.of(new SimpleGrantedAuthority("USER")));
            accessor.setUser(auth);
        }

        return message;
    }
}

