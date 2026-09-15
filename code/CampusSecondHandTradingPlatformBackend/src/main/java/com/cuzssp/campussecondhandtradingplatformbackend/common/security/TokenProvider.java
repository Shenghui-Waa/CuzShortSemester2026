package com.cuzssp.campussecondhandtradingplatformbackend.common.security;

import com.cuzssp.campussecondhandtradingplatformbackend.common.config.JwtConfig;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatformbackend.common.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private final JwtConfig jwtConfig;

    // 生成 Token
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration());
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("username", user.getUsername())
                .claim("role", user.getRole())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecret())
                .compact();
    }

    // 解析 Token 获取用户 ID
    public Long getUserIdFromToken(String token) {
        if (validateToken(token)) {
            Claims claims = parseToken(token);
            return Long.parseLong(claims.getSubject());
        }
        throw new BusinessException("Invalid token");
    }

    // 解析 Token 获取用户名
    public String getUsernameFromToken(String token) {
        if (validateToken(token)) {
            Claims claims = parseToken(token);
            return claims.get("username", String.class);
        }
        throw new BusinessException("Invalid token");
    }

    // 验证 Token 有效性
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
                .setSigningKey(jwtConfig.getSecret())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
