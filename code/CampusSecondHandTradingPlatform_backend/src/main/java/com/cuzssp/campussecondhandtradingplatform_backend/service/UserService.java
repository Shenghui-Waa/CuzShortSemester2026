package com.cuzssp.campussecondhandtradingplatform_backend.service;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.ChangePasswordRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.RegisterRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.UpdateProfileRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.PageResult;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.UserVO;

public interface UserService {
    Result<UserVO> getUserById(Long id);
    Result<UserVO> updateProfile(Long userId, UpdateProfileRequest request);
    Result<Void> changePassword(Long userId, ChangePasswordRequest request);
    Result<Void> updateAvatar(Long userId, String imageURL);

    Result<UserVO> addAdmin(RegisterRequest request);
    Result<PageResult<UserVO>> getUserList(Integer page, Integer pageSize, String keyword);
    Result<Void> updateUserStatus(Long userId, Integer targetStatus);
}
