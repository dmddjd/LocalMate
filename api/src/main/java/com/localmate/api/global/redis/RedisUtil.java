package com.localmate.api.global.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final StringRedisTemplate redisTemplate;

    // 데이터 저장(이메일, 인증번호, 유효시간)
    public void setDataExpire(String key, String value, long duration) {
        redisTemplate.opsForValue().set(key, value, duration, TimeUnit.SECONDS);
    }

    // 데이터 가져오기(인증번호 확인용)
    public String getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 데이터 삭제(인증 완료 후 삭제)
    public void deleteData(String key) {
        redisTemplate.delete(key);
    }
}
