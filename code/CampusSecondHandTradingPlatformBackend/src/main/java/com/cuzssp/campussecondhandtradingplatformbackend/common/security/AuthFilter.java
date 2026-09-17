package com.cuzssp.campussecondhandtradingplatformbackend.common.security;

import com.alibaba.fastjson.JSON;
import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.UserConstant;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.Result;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatformbackend.common.exception.BusinessException;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.UserMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final UserMapper userMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        SecurityContextHolder.clearContext();

        Long userId = isLogoutRequest(request)
                ? resolveLogoutUserId(token, response)
                : resolveUserId(token, response);
        if (userId == null) return;

        User user = userMapper.selectById(userId);

        if (user == null) {
            writeError(response, Result.Code.UNAUTHORIZED, "用户不存在");
            return;
        }

        if (!Integer.valueOf(UserConstant.Status.ACTIVE).equals(user.getStatus())) {
            writeError(response, Result.Code.FORBIDDEN, "账户被封禁");
            return;
        }

        String role = Objects.equals(
                user.getRole(), UserConstant.Role.ADMIN
        ) ? "ROLE_ADMIN" : "ROLE_USER";

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority(role))
                );
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private void writeError(
            HttpServletResponse response, Integer code, String message
    ) throws IOException {
        SecurityContextHolder.clearContext();
        response.setStatus(code);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json");

        if (Result.Code.UNAUTHORIZED.equals(code)) {
            response.setHeader("WWW-Authenticate", "Bearer");
        }

        response.getWriter()
                .write(JSON.toJSONString(Result.error(code, message)));
    }

    private Long resolveUserId(
            String token,
            HttpServletResponse response
    ) throws IOException {
        try {
            return tokenProvider.getUserId(token);
        } catch (BusinessException
                 | IllegalArgumentException
                 | JwtException exception) {
            writeError(response,
                    Result.Code.UNAUTHORIZED,
                    "用户凭证无效或过期");
            return null;
        }
    }

    private Long resolveLogoutUserId(
            String token,
            HttpServletResponse response
    ) throws IOException {
        try {
            return tokenProvider.getUserIdForLogout(token);
        } catch (BusinessException
                 | IllegalArgumentException
                 | JwtException exception) {
            writeError(response,
                    Result.Code.UNAUTHORIZED,
                    "用户凭证无效或过期");
            return null;
        }
    }

    private boolean isLogoutRequest(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod())
                && "/api/auth/logout".equals(request.getRequestURI());
    }

}
