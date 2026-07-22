package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;

import com.cuzssp.campussecondhandtradingplatform_backend.common.constant.UserConstant;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.LoginRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.RegisterRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatform_backend.common.exception.BusinessException;
import com.cuzssp.campussecondhandtradingplatform_backend.common.security.PasswordProvider;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToEntityUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToVOUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.UserMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.common.security.JwtTokenProvider;
import com.cuzssp.campussecondhandtradingplatform_backend.service.AuthService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.Result;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordProvider passwordProvider;
    private final JwtTokenProvider jwtTokenProvider;

    // 注册
    @Override
    public Result<UserVO> register(RegisterRequest request) {
        if (userMapper.countByUsername(request.getUsername()) > 0)
            throw new BusinessException("Username already exists");
        User user = ToEntityUtil.toUserEntity(request, passwordProvider);
        userMapper.insert(user);
        log.info("User registered: {}", user.getUsername());
        return Result.success(ToVOUtil.toUserVO(user));
    }

    // 登录
    @Override
    public Result<String> login(LoginRequest request) {
        User user = userMapper.selectByUsername(request.getUsername());

        if (user == null)
            throw new BusinessException("Invalid username or password");
        if (user.getStatus() == UserConstant.STATUS_DISABLE)
            throw new BusinessException("Account has been disabled");
        if (!passwordProvider.matches(request.getPassword(), user.getPassword()))
            throw new BusinessException("Invalid username or password");

        String token = jwtTokenProvider.generateToken(user);
        log.info("User logged in: {}", user.getUsername());

        return Result.success("Login successful", token);
    }

    // 登出
    @Override
    public Result<Void> logout(Long userId) {
        return Result.success();
    }

    // 获取账号信息
    public Result<UserVO> me (Long userId) {
        User user = userMapper.selectById(userId);
        return Result.success(ToVOUtil.toUserVO(user));
    }
}
