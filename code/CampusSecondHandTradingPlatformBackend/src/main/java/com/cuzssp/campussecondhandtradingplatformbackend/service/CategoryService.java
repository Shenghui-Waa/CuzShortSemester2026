package com.cuzssp.campussecondhandtradingplatformbackend.service;

import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Category;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.CategoryVO;
import java.util.List;

public interface CategoryService {
    List<CategoryVO> getAllCategories();
    // 管理员操作
    Category createCategory(Category category);
    Category updateCategory(Long id, Category category);
    Void removeCategory(Long id);
}
