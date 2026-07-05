package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.UserMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.service.UserService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.UserVO;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.UpdateProfileRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.ChangePasswordRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.exception.BusinessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserServiceImpl implements UserService {
    @Autowired private UserMapper userMapper;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public Result<UserVO> getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) throw new BusinessException("User not found");
        return Result.success(toVO(user));
    }

    @Override
    public Result<UserVO> updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("User not found");
        if (request.getNickname() != null) user.setNickname(request.getNickname());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getSchool() != null) user.setSchool(request.getSchool());
        if (request.getCampus() != null) user.setCampus(request.getCampus());
        userMapper.updateById(user);
        return Result.success(toVO(userMapper.selectById(userId)));
    }

    @Override
    public Result<Void> changePassword(Long userId, ChangePasswordRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("User not found");
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword()))
            throw new BusinessException("Old password is incorrect");
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userMapper.updateById(user);
        return Result.success();
    }

    @Override
    public Result<String> uploadAvatar(Long userId, MultipartFile file) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("User not found");
        return Result.success("ok");
    }

    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId()); vo.setUsername(user.getUsername()); vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar()); vo.setPhone(user.getPhone()); vo.setEmail(user.getEmail());
        vo.setSchool(user.getSchool()); vo.setCampus(user.getCampus()); vo.setRole(user.getRole());
        vo.setStatus(user.getStatus()); vo.setCreditScore(user.getCreditScore()); vo.setCreatedAt(user.getCreatedAt());
        return vo;
    }
}
