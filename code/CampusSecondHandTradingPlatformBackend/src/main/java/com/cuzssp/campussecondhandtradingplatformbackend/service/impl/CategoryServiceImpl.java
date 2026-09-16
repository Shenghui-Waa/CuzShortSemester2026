package com.cuzssp.campussecondhandtradingplatformbackend.service.impl;

import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.Result;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.CategoryRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Category;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.ToEntityUtil;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.ToVOUtil;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.CategoryMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.ProductMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.service.CategoryService;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.CategoryVO;
import com.cuzssp.campussecondhandtradingplatformbackend.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    // 获取分类
    @Override
    public List<CategoryVO> getAllCategories() {
        List<Category> categories = categoryMapper.selectAll();
        List<CategoryVO> categoryVOs = categories.stream()
                .map(category -> ToVOUtil.toCategoryVO(
                        category,
                        productMapper.countByCategoryId(category.getId()))
                )
                .collect(Collectors.toList());
        return categoryVOs;
    }

    // 管理员操作

    // 创建分类
    @Override
    public Category createCategory(
            CategoryRequest request
    ) {
        if (categoryMapper.selectByName(request.getName()) > 0)
            throw new BusinessException(Result.Code.FORBIDDEN, "Category already exists");

        Category category = ToEntityUtil.toCategoryEntity(request);
        categoryMapper.insert(category);
        return category;
    }

    // 修改分类
    @Override
    public Category updateCategory(Long id, CategoryRequest request) {
        Category existing = categoryMapper.selectById(id);

        if (existing == null)
            throw new BusinessException(Result.Code.NOT_FOUND, "Category not found");

        Category category = ToEntityUtil.toCategoryEntity(request);
        category.setId(id);
        category.setCreatedAt(existing.getCreatedAt());
        categoryMapper.updateById(category);
        return categoryMapper.selectById(id);
    }

    // 删除分类
    @Override
    public Void removeCategory(Long id) {
        if (productMapper.countByCategoryId(id) > 0)
            throw new BusinessException(Result.Code.FORBIDDEN, "Category has products");

        categoryMapper.deleteById(id);
        return null;
    }
}
