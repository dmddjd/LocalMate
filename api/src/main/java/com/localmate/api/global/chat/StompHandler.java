package com.localmate.api.global.chat;

import com.localmate.api.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
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

            // 인증 토큰 가져옴
            String token = accessor.getFirstNativeHeader("Authorization");

            // 토큰에서 userId를 꺼내 인증 객체를 만들고 WebSocket session에 저장
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                if(jwtUtil.validateAccessToken(token)) {
                    Long userId = jwtUtil.getUserId(token);
                    Authentication auth = new UsernamePasswordAuthenticationToken(userId, null, List.of(new SimpleGrantedAuthority("USER")));
                    accessor.setUser(auth);
                }
            }
        }
        return message;
    }
}

