package com.cuzssp.campussecondhandtradingplatform_backend.service;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.ChangePasswordRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.UpdateProfileRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    Result<UserVO> getUserById(Long id);
    Result<UserVO> updateProfile(Long userId, UpdateProfileRequest request);
    Result<Void> changePassword(Long userId, ChangePasswordRequest request);
    Result<String> uploadAvatar(Long userId, MultipartFile file);
}
