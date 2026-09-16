package com.cuzssp.campussecondhandtradingplatformbackend.service.impl;

import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.UserConstant;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.Result;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.LoginRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.UserRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatformbackend.common.exception.BusinessException;
import com.cuzssp.campussecondhandtradingplatformbackend.common.security.PasswordProvider;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.ToEntityUtil;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.ToVOUtil;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.UserMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.common.security.TokenProvider;
import com.cuzssp.campussecondhandtradingplatformbackend.service.AuthService;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordProvider passwordProvider;
    private final TokenProvider tokenProvider;

    // 注册
    @Override
    public UserVO register(UserRequest request) {
        if (userMapper.countByUsername(request.getUsername()) > 0)
            throw new BusinessException("Username already exists");

        User user = ToEntityUtil.toUserEntity(
                request, passwordProvider, UserConstant.Role.USER);
        userMapper.insert(user);
        log.info("User registered: {}", user.getUsername());
        return ToVOUtil.toUserVO(user);
    }

    // 登录
    @Override
    public String login(LoginRequest request) {
        User user = userMapper.selectByUsername(request.getUsername());

        if (user == null)
            throw new BusinessException("Invalid username or password");

        if (user.getStatus() == UserConstant.Status.INACTIVE)
            throw new BusinessException("Account has been disabled");

        if (!passwordProvider.matches(request.getPassword(), user.getPassword()))
            throw new BusinessException("Invalid username or password");

        String token = tokenProvider.generateToken(user);
        log.info("User logged in: {}", user.getUsername());

        return token;
    }

    // 登出
    @Override
    public Void logout(String token) {
        tokenProvider.revoke(token);
        return null;
    }

    // 获取账号信息
    @Override
    public UserVO me(Long userId) {
        User user = userMapper.selectById(userId);

        if (user == null)
            throw new BusinessException(Result.Code.NOT_FOUND, "User not found");

        return ToVOUtil.toUserVO(user);
    }
}
