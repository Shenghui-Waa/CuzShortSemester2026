package com.cuzssp.campussecondhandtradingplatform_backend;

import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatform_backend.common.security.PasswordProvider;
import com.cuzssp.campussecondhandtradingplatform_backend.common.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

import static java.lang.Thread.sleep;

@SpringBootTest
class CampusSecondHandTradingPlatformBackendApplicationTests {

    @Test
    void contextLoads() {

    }

    @Test
    void tokenProviderTest() throws Exception {
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider("tsyanst-cuzssp-only-test-use-default", 10);
        User user = new User();
        user.setId(1L);
        user.setUsername("tsyanst");
        user.setPassword("tsyanst");
        user.setEmail("tsyanst");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        String token = jwtTokenProvider.generateToken(user);

        System.out.println(token);

        System.out.println(jwtTokenProvider.validateToken(token));
        sleep(9000);
        System.out.println(jwtTokenProvider.validateToken(token));
        sleep(1000);
        System.out.println(jwtTokenProvider.validateToken(token));
    }

    @Test
    void BCryptPasswordEncoderTest() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        System.out.println(bCryptPasswordEncoder.encode("admin123"));
    }
}
