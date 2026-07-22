package com.cuzssp.campussecondhandtradingplatform_backend.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final JwtTokenProvider jwtTokenProvider;

    // 从 Authorization header 中提取当前用户 ID
    public Long getCurrentUserId(String token) {
        if (token == null || token.isEmpty()) return null;
        try {
            return jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        } catch (Exception e) {
            return null;
        }
    }
}