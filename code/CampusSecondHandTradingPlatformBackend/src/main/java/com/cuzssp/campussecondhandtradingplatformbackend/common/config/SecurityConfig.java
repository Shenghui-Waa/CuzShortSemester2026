package com.cuzssp.campussecondhandtradingplatformbackend.common.config;

import com.cuzssp.campussecondhandtradingplatformbackend.common.security.AuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

    private final CorsConfigurationSource corsConfigurationSource;
    private final AuthFilter authFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .anonymous(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // 1 静态资源 & 预检请求
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/", "/index.html", "/assets/**", "/favicon.ico").permitAll()

                        // 2. 匿名可访问：登录 / 注册 / WebSocket / AI
                        .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/api/ai/**").permitAll()

                        // 3. 分类：读公开，写 ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/category/**").permitAll()
                        .requestMatchers("/api/category/**").hasRole("ADMIN")

                        // 4. 商品：读公开，写需登录
                        .requestMatchers(HttpMethod.GET, "/api/product/**").permitAll()
                        .requestMatchers("/api/product/**").authenticated()

                        // 5. 评价：用户评价列表公开，其余需登录
                        .requestMatchers(HttpMethod.GET, "/api/review/user/**").permitAll()
                        .requestMatchers("/api/review/**").authenticated()

                        // 6. 公告：读公开，写 ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/announcement/**").permitAll()
                        .requestMatchers("/api/announcement/**").hasRole("ADMIN")

                        // 7. 文件：需登录
                        .requestMatchers("/api/files/**").authenticated()

                        // 8. 用户中心：需登录
                        .requestMatchers("/api/user/**").authenticated()

                        // 9. 管理端：ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 10. 兜底
                        .anyRequest().authenticated()
                )
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
