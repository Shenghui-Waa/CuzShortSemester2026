package com.cuzssp.campussecondhandtradingplatform_backend.service;

import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.Category;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.CategoryVO;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import java.util.List;

public interface CategoryService {
    Result<List<CategoryVO>> getAllCategories();
    // 管理员操作
    Result<Category> createCategory(Category category);
    Result<Category> updateCategory(Long id, Category category);
    Result<Void> deleteCategory(Long id);
}
