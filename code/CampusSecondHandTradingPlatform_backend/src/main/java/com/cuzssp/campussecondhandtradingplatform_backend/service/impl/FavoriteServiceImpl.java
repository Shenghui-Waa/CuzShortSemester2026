package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.*;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToEntityUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToVOUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.*;
import com.cuzssp.campussecondhandtradingplatform_backend.service.FavoriteService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
    private final FavoriteMapper favoriteMapper;
    private final ProductMapper productMapper;
    private final ProductImageMapper productImageMapper;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;

    /**
     * 加喜欢
     * @param userId
     * @param productId
     * @return
     */
    @Override
    public Result<Void> addFavorite(
            Long userId, Long productId
    ) {
        if (favoriteMapper.countByUserIdAndProductId(userId, productId) > 0)
            return Result.success();
        Favorite favorite = ToEntityUtil.toFavoriteEntity(userId, productId);
        favoriteMapper.insertFavorite(favorite);
        return Result.success();
    }

    /**
     * 移除喜欢
     * @param userId
     * @param productId
     * @return
     */
    @Override
    public Result<Void> removeFavorite(
            Long userId, Long productId
    ) {
        favoriteMapper.deleteByUserIdAndProductId(userId, productId);
        return Result.success();
    }

    /**
     * 校验是否喜欢
     * @param userId
     * @param productId
     * @return
     */
    @Override
    public Result<Boolean> isFavorited(
            Long userId, Long productId
    ) {
        return Result.success(
                favoriteMapper.countByUserIdAndProductId(userId, productId) > 0
        );
    }

    /**
     * 获取喜欢列表
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Result<PageResult<ProductVO>> getFavorites(
            Long userId, Integer page, Integer pageSize
    ) {
        PageHelper.startPage(page, pageSize);
        List<Favorite> favoriteList = favoriteMapper.selectByUserId(userId);
        PageInfo<Favorite> pageInfo = new PageInfo<>(favoriteList);
        List<ProductVO> productVOs = favoriteList.stream()
                .map(favorite -> {
                    Product product = productMapper.selectById(favorite.getProductId());
                    if (product == null)
                        return null;
                    ProductVO productVO = ToVOUtil.toProductVO(
                            product,
                            userMapper.selectById(product.getUserId()),
                            categoryMapper.selectById(product.getCategoryId())
                    );
                    productVO.setImages(
                            productImageMapper.selectByProductId(product.getId())
                                    .stream()
                                    .map(ProductImage::getUrl)
                                    .collect(Collectors.toList())
                    );
                    return productVO;
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());
        return Result.success(
                new PageResult<>(
                        productVOs,
                        pageInfo.getTotal(),
                        pageInfo.getPageNum(),
                        pageInfo.getPageSize()
                )
        );
    }
}
