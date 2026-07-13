package com.cuzssp.campussecondhandtradingplatform_backend.controller;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.LoginRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.RegisterRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.security.JwtTokenProvider;
import com.cuzssp.campussecondhandtradingplatform_backend.service.AuthService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthService authService;
    @Autowired private JwtTokenProvider jwtTokenProvider;

    /**
     * 注册
     * @param request 请求
     * @return
     */
    @PostMapping("/register")
    public Result<?> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return authService.register(request);
    }

    /**
     * 登录
     * @param request
     * @return
     */
    @PostMapping("/login")
    public Result<?> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return authService.login(request);
    }

    /**
     * 登出
     * @param token
     * @return
     */
    @PostMapping("/logout")
    public Result<?> logout(
            @RequestHeader("Authorization") String token
    ) {
        Long currentUserId = getCurrentUserId(token);
        return authService.logout(currentUserId);
    }

    /**
     * 是不是我？
     * @param token
     * @return
     */
    @GetMapping("/me")
    public Result<?> me(
            @RequestHeader("Authorization") String token
    ) {
        Long currentUserId = getCurrentUserId(token);
        return authService.me(currentUserId);
    }

    private Long getCurrentUserId(String token) {
        if (token == null || token.isEmpty())
            return null;
        return jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
    }
}