package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.Category;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.CategoryMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.ProductMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.service.CategoryService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.CategoryVO;
import com.cuzssp.campussecondhandtradingplatform_backend.common.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired private CategoryMapper categoryMapper;
    @Autowired private ProductMapper productMapper;

    @Override
    public Result<List<CategoryVO>> getAllCategories() {
        List<Category> categories = categoryMapper.selectAll();
        List<CategoryVO> vos = categories.stream().map(cat -> {
            CategoryVO vo = new CategoryVO();
            vo.setId(cat.getId()); vo.setName(cat.getName()); vo.setIcon(cat.getIcon());
            vo.setSortOrder(cat.getSortOrder());
            vo.setProductCount((long) productMapper.countByCategoryId(cat.getId()));
            return vo;
        }).collect(Collectors.toList());
        return Result.success(vos);
    }

    @Override
    public Result<Category> createCategory(Category category) {
        categoryMapper.insert(category);
        return Result.success(category);
    }

    @Override
    public Result<Category> updateCategory(Long id, Category category) {
        Category existing = categoryMapper.selectById(id);
        if (existing == null) throw new BusinessException("Category not found");
        category.setId(id);
        categoryMapper.updateById(category);
        return Result.success(categoryMapper.selectById(id));
    }

    @Override
    public Result<Void> deleteCategory(Long id) {
        if (productMapper.countByCategoryId(id) > 0) throw new BusinessException("Category has products");
        categoryMapper.deleteById(id);
        return Result.success();
    }
}
