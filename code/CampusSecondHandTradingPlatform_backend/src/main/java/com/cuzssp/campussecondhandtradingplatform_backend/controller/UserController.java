package com.cuzssp.campussecondhandtradingplatform_backend.controller;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.ChangePasswordRequest;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.UpdateProfileRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.security.JwtTokenProvider;
import com.cuzssp.campussecondhandtradingplatform_backend.service.UserService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/{id}")
    public Result<?> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestHeader("Authorization") String token,
                                    @RequestBody UpdateProfileRequest request) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        return userService.updateProfile(userId, request);
    }

    @PutMapping("/password")
    public Result<?> changePassword(@RequestHeader("Authorization") String token,
                                     @RequestBody ChangePasswordRequest request) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        return userService.changePassword(userId, request);
    }

    @PostMapping("/avatar")
    public Result<?> uploadAvatar(@RequestHeader("Authorization") String token,
                                   @RequestParam("file") MultipartFile file) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        return userService.uploadAvatar(userId, file);
    }
}
