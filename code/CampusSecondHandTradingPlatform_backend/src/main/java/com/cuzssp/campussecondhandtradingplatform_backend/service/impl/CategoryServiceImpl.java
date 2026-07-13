package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.Category;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToVOUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.CategoryMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.ProductMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.service.CategoryService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.CategoryVO;
import com.cuzssp.campussecondhandtradingplatform_backend.common.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired private CategoryMapper categoryMapper;
    @Autowired private ProductMapper productMapper;

    /**
     * 获取所有分类
     * @return
     */
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

    /**
     * 创建分类
     * @param category
     * @return
     */
    @Override
    public Result<Category> createCategory(
            Category category
    ) {
        if (categoryMapper.selectByName(category.getName()) > 0)
            throw new BusinessException("Category already exists");
        category.setCreatedAt(LocalDateTime.now());
        categoryMapper.insertCategory(category);
        return Result.success(category);
    }

    /**
     * 修改分类
     * @param id
     * @param category
     * @return
     */
    @Override
    public Result<Category> updateCategory(Long id, Category category) {
        Category existing = categoryMapper.selectById(id);
        if (existing == null)
            throw new BusinessException("Category not found");
        category.setId(id);
        categoryMapper.updateById(category);
        return Result.success(categoryMapper.selectById(id));
    }

    /**
     * 删除分类
     * @param id
     * @return
     */
    @Override
    public Result<Void> deleteCategory(Long id) {
        if (productMapper.countByCategoryId(id) > 0)
            throw new BusinessException("Category has products");
        categoryMapper.deleteById(id);
        return Result.success();
    }
}
