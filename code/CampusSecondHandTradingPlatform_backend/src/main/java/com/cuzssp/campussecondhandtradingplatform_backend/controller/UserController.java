package com.cuzssp.campussecondhandtradingplatform_backend.controller;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.ChangePasswordRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.UpdateProfileRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.security.SecurityUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.service.UserService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SecurityUtil securityUtil;

    /**
     * 获取用户信息
     */
    @GetMapping("/{id}")
    public Result<?> getUserById(
            @PathVariable Long id
    ) {
        return userService.getUserById(id);
    }

    /**
     * 修改个人信息
     */
    @PutMapping("/profile")
    public Result<?> updateProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody UpdateProfileRequest request
    ) {
        Long currentUserId = securityUtil.getCurrentUserId(token);
        return userService.updateProfile(currentUserId, request);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<?> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody ChangePasswordRequest request
    ) {
        Long currentUserId = securityUtil.getCurrentUserId(token);
        return userService.changePassword(currentUserId, request);
    }

    /**
     * 修改头像
     */
    @PostMapping("/avatar")
    public Result<?> uploadAvatar(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String image
    ) {
        Long currentUserId = securityUtil.getCurrentUserId(token);
        return userService.updateAvatar(currentUserId, image);
    }

}
