package com.cuzssp.campussecondhandtradingplatformbackend.service;

import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.LoginRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.UserRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.UserVO;

public interface AuthService {

    UserVO register(UserRequest request);
    String login(LoginRequest request);
    Void logout(String token);
    UserVO me(Long userId);
}
