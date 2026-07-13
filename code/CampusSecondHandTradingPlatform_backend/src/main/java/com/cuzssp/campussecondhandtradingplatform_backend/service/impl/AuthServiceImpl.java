package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;
import com.cuzssp.campussecondhandtradingplatform_backend.common.constant.UserConstant;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.LoginRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.RegisterRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatform_backend.common.exception.BusinessException;
import com.cuzssp.campussecondhandtradingplatform_backend.common.security.Base64Provider;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToEntityUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToVOUtil;
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

    /**
     * 注册
     * @param request
     * @return
     */
    @Override
    public Result<UserVO> register(RegisterRequest request) {
        if (userMapper.countByUsername(request.getUsername()) > 0)
            throw new BusinessException("Username already exists");
        User user = ToEntityUtil.toUserEntity(request, base64Provider);
        userMapper.insert(user);
        log.info("User registered: {}", user.getUsername());
        return Result.success(ToVOUtil.toUserVO(user));
    }

    /**
     * 登录
     * @param request
     * @return
     */
    @Override
    public Result<String> login(LoginRequest request) {
        User user = userMapper.selectByUsername(request.getUsername());

        if (user == null)
            throw new BusinessException("Invalid username or password");
        if (user.getStatus() == UserConstant.STATUS_DISABLE)
            throw new BusinessException("Account has been disabled");
        if (!base64Provider.matches(request.getPassword(), user.getPassword()))
            throw new BusinessException("Invalid username or password");

        String token = jwtTokenProvider.generateToken(user);
        log.info("User logged in: {}", user.getUsername());

        return Result.success("Login successful", token);
    }

    /**
     * 登出
     * @param userId
     * @return
     */
    @Override
    public Result<Void> logout(Long userId) {
        return Result.success();
    }

    /**
     * 是不是我？
     * @param userId
     * @return
     */
    public Result<UserVO> me (Long userId) {
        User user = userMapper.selectById(userId);
        return Result.success(ToVOUtil.toUserVO(user));
    }
}
