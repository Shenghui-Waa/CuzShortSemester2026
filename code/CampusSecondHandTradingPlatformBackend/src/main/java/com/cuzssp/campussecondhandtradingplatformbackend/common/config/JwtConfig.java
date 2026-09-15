package com.cuzssp.campussecondhandtradingplatformbackend.common.config;

import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    private SecretKey secret;
    private Long expiration;

    public void setSecret(String secret) throws NoSuchAlgorithmException {
        byte[] keyBytes = java.security
                .MessageDigest.getInstance("SHA-256")
                .digest(secret.getBytes(StandardCharsets.UTF_8));
        this.secret = Keys.hmacShaKeyFor(keyBytes);
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration * 1000;
    }

}
