package com.cuzssp.campussecondhandtradingplatform_backend.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
@RequiredArgsConstructor
public class PasswordProvider {

    private final PasswordEncoder passwordEncoder;

    /**
     * 加密
     * @param password 原文
     * @return 密文
     */
    public String encode(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * 校验
     * @param rawPassword 原文
     * @param encodedPassword 密文
     * @return 布尔值 是否相同
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return rawPassword != null && encodedPassword != null
                && passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
