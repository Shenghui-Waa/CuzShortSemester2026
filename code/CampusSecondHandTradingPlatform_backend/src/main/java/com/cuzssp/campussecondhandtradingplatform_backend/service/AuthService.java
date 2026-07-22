package com.cuzssp.campussecondhandtradingplatform_backend.service;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.LoginRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.RegisterRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.Result;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.UserVO;

public interface AuthService {

    Result<UserVO> register(RegisterRequest request);
    Result<String> login(LoginRequest request);
    Result<Void> logout(Long userId);
    Result<UserVO> me(Long userId);

}
