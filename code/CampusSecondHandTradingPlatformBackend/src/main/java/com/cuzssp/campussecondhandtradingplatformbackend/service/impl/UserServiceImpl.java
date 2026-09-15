package com.cuzssp.campussecondhandtradingplatformbackend.service.impl;

import com.cuzssp.campussecondhandtradingplatformbackend.common.security.PasswordProvider;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.ProductMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.UserMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordProvider passwordProvider;
    private final ProductMapper productMapper;

    private static final int BATCH_SIZE = 50;

}
