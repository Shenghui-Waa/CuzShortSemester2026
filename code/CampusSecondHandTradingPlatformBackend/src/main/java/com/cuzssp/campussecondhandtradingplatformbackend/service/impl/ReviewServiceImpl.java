package com.cuzssp.campussecondhandtradingplatformbackend.service.impl;

import com.cuzssp.campussecondhandtradingplatformbackend.mapper.OrderInfoMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.ReviewMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.UserMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final OrderInfoMapper orderInfoMapper;
    private final UserMapper userMapper;

}
