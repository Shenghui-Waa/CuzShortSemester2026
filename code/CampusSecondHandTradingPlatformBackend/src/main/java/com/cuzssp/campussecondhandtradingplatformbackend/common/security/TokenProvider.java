package com.cuzssp.campussecondhandtradingplatformbackend.common.security;

import com.cuzssp.campussecondhandtradingplatformbackend.common.config.JwtConfig;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatformbackend.common.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
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
                .signWith(jwtConfig.getSecret())
                .compact();
    }

    // 解析 Token 获取用户 ID
    public Long getUserId(String token) {
        if (!validate(token))
            throw new BusinessException("Invalid token");
        return Long.parseLong(parse(token).getSubject());

    }

    // 解析 Token 获取用户名
    public String getUsername(String token) {
        if (!validate(token))
            throw new BusinessException("Invalid token");
        return parse(token).get("username", String.class);

    }

    public Integer getRole(String token) {
        if (!validate(token))
            throw new BusinessException("Invalid token");
        return parse(token).get("role", Integer.class);

    }

    // 验证 Token 有效性
    public boolean validate(String token) {
        if (token == null || token.isEmpty()) return false;
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parse(String token) {
        if (token != null && token.startsWith("Bearer "))
            token = token.substring("Bearer ".length());
        return Jwts.parser()
                .verifyWith(jwtConfig.getSecret())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
