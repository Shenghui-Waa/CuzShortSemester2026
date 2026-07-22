package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;

import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.Category;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToVOUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.CategoryMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.ProductMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.service.CategoryService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.Result;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.CategoryVO;
import com.cuzssp.campussecondhandtradingplatform_backend.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    // 获取分类
    @Override
    public Result<List<CategoryVO>> getAllCategories() {
        List<Category> categories = categoryMapper.selectAll();
        List<CategoryVO> categoryVOs = categories.stream()
                .map(category -> ToVOUtil.toCategoryVO(
                        category,
                        productMapper.countByCategoryId(category.getId()))
                ).collect(Collectors.toList());
        return Result.success(categoryVOs);
    }


    // =====================================================================================
    // ===========================>>>>> 管 理 员 操 作 <<<<<==================================
    // =====================================================================================

    // 创建分类
    @Override
    public Result<Category> createCategory(
            Category category
    ) {
        if (categoryMapper.selectByName(category.getName()) > 0)
            throw new BusinessException(403, "Category already exists");
        category.setCreatedAt(LocalDateTime.now());
        categoryMapper.insertCategory(category);
        return Result.success(category);
    }

    // 修改分类
    @Override
    public Result<Category> updateCategory(Long id, Category category) {
        Category existing = categoryMapper.selectById(id);
        if (existing == null)
            throw new BusinessException(404, "Category not found");
        category.setId(id);
        categoryMapper.updateById(category);
        return Result.success(categoryMapper.selectById(id));
    }

    // 删除分类
    @Override
    public Result<Void> removeCategory(Long id) {
        if (productMapper.countByCategoryId(id) > 0)
            throw new BusinessException(403, "Category has products");
        categoryMapper.deleteById(id);
        return Result.success();
    }
}
