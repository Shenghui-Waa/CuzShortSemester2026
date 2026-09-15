package com.cuzssp.campussecondhandtradingplatform_backend.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
@RequiredArgsConstructor
public class PasswordProvider {

    private final PasswordEncoder passwordEncoder;

    // 密码加密
    public String encode(String password) {
        return passwordEncoder.encode(password);
    }

    // 密码校验
    public boolean matches(String rawPassword, String encodedPassword) {
        return rawPassword != null && encodedPassword != null
                && passwordEncoder.matches(rawPassword, encodedPassword);
    }

}
