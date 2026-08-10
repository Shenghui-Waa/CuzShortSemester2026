package com.cuzssp.campussecondhandtradingplatform_backend.service;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.*;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.PageResult;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.UserVO;
import jakarta.validation.Valid;

public interface UserService {

    Result<UserVO> getUserById(Long id);
    Result<UserVO> updateProfile(Long userId, UpdateProfileRequest request);
    Result<Void> changePassword(Long userId, ChangePasswordRequest request);
    Result<Void> updateAvatar(Long userId, String imageURL);
    // 管理员操作
    Result<PageResult<UserVO>> getUserList(Integer page, Integer pageSize, String keyword);
    Result<UserVO> addAdmin(RegisterRequest request);
    Result<Void> updateUserStatus(Long userId, Integer targetStatus);
    Result<Void> deleteUserById(Long id);
    Result<Void> resetPassword(Long id, ResetPasswordRequest request);
}
