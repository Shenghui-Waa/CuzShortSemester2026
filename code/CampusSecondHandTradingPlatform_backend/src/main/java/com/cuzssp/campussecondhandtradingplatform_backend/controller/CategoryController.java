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

    /**
     * 创建分类信息
     * @param category
     * @return
     */
    @PostMapping
    public Result<?> createCategory(
            @RequestBody Category category
    ) {
        return categoryService.createCategory(category);
    }

    /**
     * 修改分类信息 基于 id
     * @param id
     * @param category
     * @return
     */
    @PutMapping("/{id}")
    public Result<?> updateCategory(
            @PathVariable Long id,
            @RequestBody Category category
    ) {
        return categoryService.updateCategory(id, category);
    }

    /**
     * 删除分类信息 基于 id
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteCategory(
            @PathVariable Long id
    ) {
        return categoryService.deleteCategory(id);
    }


}
