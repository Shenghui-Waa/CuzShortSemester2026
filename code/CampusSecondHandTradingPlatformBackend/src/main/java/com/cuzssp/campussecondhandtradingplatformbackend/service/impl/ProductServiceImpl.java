package com.cuzssp.campussecondhandtradingplatformbackend.service.impl;

import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.ProductConstant;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.*;
import com.cuzssp.campussecondhandtradingplatformbackend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductImageMapper productImageMapper;
    private final FavoriteMapper favoriteMapper;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;

    private static final Set<String> ALLOWED_TRANSITIONS = Set.of(
            ProductConstant.Status.ON_SALE + "->" + ProductConstant.Status.DISABLE,
            ProductConstant.Status.DISABLE + "->" + ProductConstant.Status.NEED_CHECK
    );

}
