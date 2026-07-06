package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.LoginRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.RegisterRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatform_backend.common.exception.BusinessException;
import com.cuzssp.campussecondhandtradingplatform_backend.common.security.Base64Provider;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.UserMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.common.security.JwtTokenProvider;
import com.cuzssp.campussecondhandtradingplatform_backend.service.AuthService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired private UserMapper userMapper;
    @Autowired private Base64Provider base64Provider;
    @Autowired private JwtTokenProvider jwtTokenProvider;

    @Override
    public Result<UserVO> register(RegisterRequest request) {
        if (userMapper.countByUsername(request.getUsername()) > 0)
            throw new BusinessException("Username already exists");
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(base64Provider.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setPhone(request.getPhone()); user.setEmail(request.getEmail());
        user.setSchool(request.getSchool()); user.setCampus(request.getCampus());
        user.setRole(0);
        user.setStatus(0);
        user.setCreditScore(100);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);
        log.info("User registered: {}", user.getUsername());
        return Result.success(toVO(user));
    }

    @Override
    public Result<String> login(LoginRequest request) {
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) throw new BusinessException("Invalid username or password");
        if (user.getStatus() == 1) throw new BusinessException("Account has been disabled");
        if (!base64Provider.matches(request.getPassword(), user.getPassword()))
            throw new BusinessException("Invalid username or password");
        String token = jwtTokenProvider.generateToken(user);
        log.info("User logged in: {}", user.getUsername());
        return Result.success("Login successful", token);
    }

    @Override
    public Result<Void> logout(Long userId) { return Result.success(); }

    @Override
    public Result<UserVO> me(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("User not found");
        return Result.success(toVO(user));
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
