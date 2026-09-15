package com.cuzssp.campussecondhandtradingplatformbackend.service.impl;

import com.cuzssp.campussecondhandtradingplatformbackend.mapper.CartItemMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.ProductImageMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.ProductMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.UserMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemMapper cartItemMapper;
    private final ProductMapper productMapper;
    private final ProductImageMapper productImageMapper;
    private final UserMapper userMapper;

}
