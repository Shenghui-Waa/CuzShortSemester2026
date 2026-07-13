package com.cuzssp.campussecondhandtradingplatform_backend.common.security;

import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatform_backend.common.exception.BusinessException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration * 1000; // 转毫秒值
    }

    /**
     * 生成 Token
     * @param user
     * @return
     */
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("username", user.getUsername())
                .claim("role", user.getRole())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * 解析 Token 获取用户 ID
     * @param token
     * @return
     */
    public Long getUserIdFromToken(String token) {
        if (validateToken(token)) {
            Claims claims = parseToken(token);
            return Long.parseLong(claims.getSubject());
        }
        throw new BusinessException("Invalid token");
    }

    /**
     * 解析 Token 获取用户名
     * @param token
     * @return
     */
    public String getUsernameFromToken(String token) {
        if (validateToken(token)) {
            Claims claims = parseToken(token);
            return claims.get("username", String.class);
        }
        throw new BusinessException("Invalid token");
    }

    /**
     * 验证 Token 有效性
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
