package com.cuzssp.campussecondhandtradingplatformbackend.service;

import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.LoginRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.RegisterRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.UserVO;

public interface AuthService {

    UserVO register(RegisterRequest request);
    String login(LoginRequest request);
    Void logout(Long userId);
    UserVO me(Long userId);
}
