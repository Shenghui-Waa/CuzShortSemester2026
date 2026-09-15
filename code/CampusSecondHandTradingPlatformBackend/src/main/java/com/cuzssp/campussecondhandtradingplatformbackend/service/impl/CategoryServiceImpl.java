package com.cuzssp.campussecondhandtradingplatformbackend.service.impl;

import com.cuzssp.campussecondhandtradingplatformbackend.mapper.CategoryMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.ProductMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

}
