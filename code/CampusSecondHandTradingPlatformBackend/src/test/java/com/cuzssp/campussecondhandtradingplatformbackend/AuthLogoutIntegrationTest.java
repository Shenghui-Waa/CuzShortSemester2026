package com.cuzssp.campussecondhandtradingplatformbackend;

import com.cuzssp.campussecondhandtradingplatformbackend.common.config.JwtConfig;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatformbackend.common.security.TokenProvider;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class AuthLogoutIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private JwtConfig jwtConfig;

    @Test
    void logoutRevokesOnlySubmittedToken() throws Exception {
        User user = insertUser("logout-current-user");
        String currentToken = tokenProvider.generateToken(user);
        String otherToken = tokenProvider.generateToken(user);

        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + currentToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + currentToken))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isOk());
    }

    @Test
    void newTokenAfterLogoutIsUsable() throws Exception {
        User user = insertUser("logout-new-token-user");
        String oldToken = tokenProvider.generateToken(user);

        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + oldToken))
                .andExpect(status().isOk());

        String newToken = tokenProvider.generateToken(user);
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + newToken))
                .andExpect(status().isOk());
    }

    @Test
    void repeatedLogoutIsIdempotent() throws Exception {
        User user = insertUser("logout-repeat-user");
        String token = tokenProvider.generateToken(user);

        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void tokenWithoutJtiIsRejected() throws Exception {
        User user = insertUser("logout-legacy-token-user");
        Date now = new Date();
        String legacyToken = Jwts.builder()
                .subject(user.getId().toString())
                .claim("username", user.getUsername())
                .claim("role", user.getRole())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + 60_000))
                .signWith(jwtConfig.getSecret())
                .compact();

        assertThat(tokenProvider.validate(legacyToken)).isFalse();
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + legacyToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidAndExpiredTokensAreRejected() throws Exception {
        User user = insertUser("logout-invalid-token-user");
        String invalidToken = tokenProvider.generateToken(user) + "invalid";

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized());

        long now = System.currentTimeMillis();
        String expiredToken = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getId().toString())
                .issuedAt(new Date(now - 120_000))
                .expiration(new Date(now - 60_000))
                .signWith(jwtConfig.getSecret())
                .compact();

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutRemovesExpiredRevocations() throws Exception {
        User user = insertUser("logout-cleanup-user");
        String token = tokenProvider.generateToken(user);
        jdbcTemplate.update(
                "INSERT INTO revoked_token (jti, expires_at) VALUES (?, ?)",
                "expired-jti",
                new Date(System.currentTimeMillis() - 60_000)
        );

        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        Integer expiredCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM revoked_token WHERE jti = ?",
                Integer.class,
                "expired-jti"
        );
        assertThat(expiredCount).isZero();
    }
}
