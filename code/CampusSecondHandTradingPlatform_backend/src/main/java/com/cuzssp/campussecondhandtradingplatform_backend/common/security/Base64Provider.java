package com.cuzssp.campussecondhandtradingplatform_backend.common.security;

import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class Base64Provider {
    public String encode(String password) {
        return Base64.getEncoder().encodeToString(("cuzssp" + password + "2026webproject").getBytes());
    }

    public String decode(String password) {
        return new String(Base64.getDecoder().decode(password))
                .replace("cuzssp", "")
                .replace("2026webproject", "");
    }

    public boolean matches(@Nullable String rawPassword, @Nullable String encodedPassword) {
        return rawPassword != null && encodedPassword != null && encode(rawPassword).equals(encodedPassword);
    }
}
