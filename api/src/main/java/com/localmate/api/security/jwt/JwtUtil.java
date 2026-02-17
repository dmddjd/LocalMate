package com.localmate.api.security.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
//        byte[] decode = Base64.getDecoder().decode(secret);
//        this.secretKey = Keys.hmacShaKeyFor(decode);
        this.secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
    }

    // Access Token 생성
    public String createAccessToken(String id, String role) {
        long expiredMs = 1000 * 60 * 30; // 30분

        return Jwts.builder()
                .claim("category", "access")
                .claim("id", id)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    // Refresh Token 생성
    public String createRefreshToken(String id) {
        long expiredMs = 1000 * 60 * 60 * 24 * 7; // 7일

        return Jwts.builder()
                .claim("category", "refresh")
                .claim("id", id)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    // id 추출
    public String getId(String token) {
        return getClaims(token).get("id", String.class);
    }

    // role 추출
    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // category 추출
    public String getCategory(String token) {
        return getClaims(token).get("category", String.class);
    }

    // 만료 여부 확인
    public boolean isExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    // Access Token 유효성 검사
    public boolean validateAccessToken(String token) {
        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date())
                    && claims.get("category", String.class).equals("access");
        } catch (Exception e) {
            return false;
        }
    }

    // Refresh Token 유효성 검사
    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date())
                    && claims.get("category", String.class).equals("refresh");
        } catch (Exception e) {
            return false;
        }
    }

    // 토큰 추출
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
