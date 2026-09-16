package com.cuzssp.campussecondhandtradingplatformbackend.controller;

import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.ChangePasswordRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.UserRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.security.TokenProvider;
import com.cuzssp.campussecondhandtradingplatformbackend.service.UserService;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.Result;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TokenProvider tokenProvider;

    /**
     * 获取用户信息
     */
    @GetMapping("/{id}")
    public Result<?> getUserById(
            @PathVariable Long id
    ) {
        return Result.success(userService.getUserById(id));
    }

    /**
     * 修改个人信息
     */
    @PutMapping("/profile")
    public Result<?> updateProfile(
            @RequestHeader("Authorization") String token,
            @Validated({Default.class, UserRequest.Update.class})
            @RequestBody UserRequest request
    ) {
        Long currentUserId = tokenProvider.getUserId(token);
        return Result.success(userService.updateProfile(currentUserId, request));
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<?> changePassword(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        Long currentUserId = tokenProvider.getUserId(token);
        return Result.success(userService.changePassword(currentUserId, request));
    }

    /**
     * 修改头像
     */
    @PostMapping("/avatar")
    public Result<?> uploadAvatar(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String image
    ) {
        Long currentUserId = tokenProvider.getUserId(token);
        return Result.success(userService.updateAvatar(currentUserId, image));
    }
}
