package com.cuzssp.campussecondhandtradingplatform_backend.common.security;

import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    private final JwtTokenProvider jwtTokenProvider;

    public SecurityUtil(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * 从 Authorization header 中提取当前用户 ID
     * @param token Bearer token 或 null
     * @return 用户 ID，解析失败或 token 为空时返回 null
     */
    public Long getCurrentUserId(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        try {
            return jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        } catch (Exception e) {
            return null;
        }
    }
}