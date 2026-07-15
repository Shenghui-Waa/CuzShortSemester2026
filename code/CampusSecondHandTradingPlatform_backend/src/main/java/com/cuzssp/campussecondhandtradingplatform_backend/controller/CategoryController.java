package com.cuzssp.campussecondhandtradingplatform_backend.controller;

import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.Category;

import com.cuzssp.campussecondhandtradingplatform_backend.service.CategoryService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired private CategoryService categoryService;

    /**
     * 获取所有分类
     * @return
     */
    @GetMapping
    public Result<?> getAllCategories() {
        return categoryService.getAllCategories();
    }


}
