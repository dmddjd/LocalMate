package com.localmate.api.global.chat;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // 웹소켓 연결 경로
                .setAllowedOriginPatterns("*") // 웹소켓 연결 요청 허용 도메인(CORS 설정)
                .withSockJS(); // 웹소켓을 지원하지 않을 경우 대체 프로토콜을 사용하여 통신
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic", "queue");
        registry.setUserDestinationPrefix("/user");
    }
}
