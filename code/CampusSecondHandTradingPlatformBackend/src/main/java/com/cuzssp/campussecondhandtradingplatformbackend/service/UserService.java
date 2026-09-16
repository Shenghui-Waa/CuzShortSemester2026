package com.cuzssp.campussecondhandtradingplatformbackend.service;

import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.ChangePasswordRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.ResetPasswordRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.UserRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.PageResult;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.UserVO;

public interface UserService {

    UserVO getUserById(Long id);
    UserVO updateProfile(Long userId, UserRequest request);
    Void changePassword(Long userId, ChangePasswordRequest request);
    Void updateAvatar(Long userId, String imageURL);
    // 管理员操作
    PageResult<UserVO> getUserList(Integer page, Integer pageSize, String keyword);
    UserVO addAdmin(UserRequest request);
    Void updateUserStatus(Long userId, Integer targetStatus);
    Void deleteUserById(Long id);
    Void resetPassword(Long id, ResetPasswordRequest request);
}
