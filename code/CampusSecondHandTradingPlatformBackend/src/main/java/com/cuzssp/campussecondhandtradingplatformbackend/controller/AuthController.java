package com.cuzssp.campussecondhandtradingplatformbackend.controller;

import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.LoginRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.RegisterRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.security.TokenProvider;
import com.cuzssp.campussecondhandtradingplatformbackend.service.AuthService;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenProvider tokenProvider;

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<?> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return Result.success(authService.register(request));
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<?> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return Result.success(authService.login(request));
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public Result<?> logout(
            @RequestHeader("Authorization") String token
    ) {
        Long currentUserId = tokenProvider.getUserId(token);
        return Result.success(authService.logout(currentUserId));
    }

    /**
     * 获取账户信息
     */
    @GetMapping("/me")
    public Result<?> me(
            @RequestHeader("Authorization") String token
    ) {
        Long currentUserId = tokenProvider.getUserId(token);
        return Result.success(authService.me(currentUserId));
    }
}
