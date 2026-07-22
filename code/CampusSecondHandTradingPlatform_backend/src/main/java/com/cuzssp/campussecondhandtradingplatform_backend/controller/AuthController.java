package com.cuzssp.campussecondhandtradingplatform_backend.controller;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.LoginRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.RegisterRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.security.SecurityUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.service.AuthService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SecurityUtil securityUtil;

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<?> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return authService.register(request);
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<?> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return authService.login(request);
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public Result<?> logout(
            @RequestHeader("Authorization") String token
    ) {
        Long currentUserId = securityUtil.getCurrentUserId(token);
        return authService.logout(currentUserId);
    }

    /**
     * 获取账户信息
     */
    @GetMapping("/me")
    public Result<?> me(
            @RequestHeader("Authorization") String token
    ) {
        Long currentUserId = securityUtil.getCurrentUserId(token);
        return authService.me(currentUserId);
    }

}