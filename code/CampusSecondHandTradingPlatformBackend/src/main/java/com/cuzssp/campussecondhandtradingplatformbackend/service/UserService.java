package com.cuzssp.campussecondhandtradingplatformbackend.service;

import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.ChangePasswordRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.UpdateProfileRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.RegisterRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.ResetPasswordRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.PageResult;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.UserVO;

public interface UserService {

    UserVO getUserById(Long id);
    UserVO updateProfile(Long userId, UpdateProfileRequest request);
    Void changePassword(Long userId, ChangePasswordRequest request);
    Void updateAvatar(Long userId, String imageURL);
    // 管理员操作
    PageResult<UserVO> getUserList(Integer page, Integer pageSize, String keyword);
    UserVO addAdmin(RegisterRequest request);
    Void updateUserStatus(Long userId, Integer targetStatus);
    Void deleteUserById(Long id);
    Void resetPassword(Long id, ResetPasswordRequest request);
}
