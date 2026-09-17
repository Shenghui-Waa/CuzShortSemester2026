package com.cuzssp.campussecondhandtradingplatformbackend.common.security;

import com.cuzssp.campussecondhandtradingplatformbackend.common.config.JwtConfig;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatformbackend.common.exception.BusinessException;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.RevokedTokenMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private final JwtConfig jwtConfig;
    private final RevokedTokenMapper revokedTokenMapper;

    // 生成 Token
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration());
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
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
        return Long.parseLong(parseValid(token).getSubject());

    }

    // 登出时允许重复处理已撤销的有效 Token
    public Long getUserIdForLogout(String token) {
        return Long.parseLong(parseForRevocation(token).getSubject());
    }

    // 解析 Token 获取用户名
    public String getUsername(String token) {
        return parseValid(token).get("username", String.class);

    }

    public Integer getRole(String token) {
        return parseValid(token).get("role", Integer.class);

    }

    // 验证 Token 有效性
    public boolean validate(String token) {
        if (token == null || token.isEmpty()) return false;
        try {
            parseValid(token);
            return true;
        } catch (JwtException | IllegalArgumentException | BusinessException e) {
            return false;
        }
    }

    // 撤销当前 Token
    public void revoke(String token) {
        Claims claims = parseForRevocation(token);
        revokedTokenMapper.deleteExpired(new Date());
        if (revokedTokenMapper.countByJti(claims.getId()) > 0) {
            return;
        }
        try {
            revokedTokenMapper.insertRevocation(
                    claims.getId(),
                    claims.getExpiration()
            );
        } catch (DataAccessException exception) {
            if (revokedTokenMapper.countByJti(claims.getId()) == 0) {
                throw exception;
            }
        }
    }

    private Claims parseValid(String token) {
        Claims claims = parseForRevocation(token);
        if (revokedTokenMapper.countByJti(claims.getId()) > 0) {
            throw new BusinessException("用户凭证无效");
        }
        return claims;
    }

    private Claims parseForRevocation(String token) {
        Claims claims = parse(token);
        String jti = claims.getId();
        if (jti == null || jti.isBlank()) {
            throw new BusinessException("用户凭证无效");
        }
        return claims;
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
