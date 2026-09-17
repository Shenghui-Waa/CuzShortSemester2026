package com.cuzssp.campussecondhandtradingplatformbackend.service.impl;
import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.ProductConstant;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.PageResult;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.Result;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.FavoriteRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Product;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Favorite;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.ToEntityUtil;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.ToVOUtil;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.FavoriteMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.ProductMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.ProductImageMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.UserMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.CategoryMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.service.FavoriteService;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.ProductVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.cuzssp.campussecondhandtradingplatformbackend.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final ProductMapper productMapper;
    private final ProductImageMapper productImageMapper;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;

    // 获取收藏
    @Override
    public PageResult<ProductVO> getFavorite(
            Long userId, Integer page, Integer pageSize
    ) {
        if (page == null || page < 1
                || pageSize == null || pageSize < 1 || pageSize > 100)
            throw new BusinessException("无效的分页");

        List<Favorite> favoriteList;
        try {
            PageHelper.startPage(page, pageSize);
            favoriteList = favoriteMapper.selectByUserId(userId);
        } finally {
            PageHelper.clearPage();
        }
        PageInfo<Favorite> pageInfo = new PageInfo<>(favoriteList);
        List<ProductVO> productVOs = favoriteList
                .stream()
                .map(favorite -> {
                    Product product = productMapper.selectById(favorite.getProductId());

                    if (product == null
                            || !Objects.equals(product.getStatus(), ProductConstant.Status.ON_SALE))
                        return null;

                    ProductVO productVO = ToVOUtil.toProductVO(
                            product, userMapper.selectById(product.getUserId()),
                            categoryMapper.selectById(product.getCategoryId()),
                            productImageMapper.selectByProductId(product.getId())
                    );
                    productVO.setIsFavorite(true);
                    return productVO;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new PageResult<>(
                productVOs, pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize()
        );
    }

    // 添加收藏
    @Override
    public Void addFavorite(
            Long userId, FavoriteRequest request
    ) {
        Product product = productMapper.selectById(request.getProductId());
        if (product == null)
            throw new BusinessException(Result.Code.NOT_FOUND, "商品不存在");

        if (!Objects.equals(product.getStatus(), ProductConstant.Status.ON_SALE))
            throw new BusinessException("商品不可用");

        if (favoriteMapper.countByUserIdAndProductId(userId, request.getProductId()) > 0)
            return null;

        Favorite favorite = ToEntityUtil.toFavoriteEntity(userId, request);
        favoriteMapper.insert(favorite);
        return null;
    }

    // 移出收藏
    @Override
    public Void removeFavorite(
            Long userId, Long productId
    ) {
        favoriteMapper.deleteByUserIdAndProductId(userId, productId);
        return null;
    }

    // 是否收藏
    @Override
    public Boolean isFavorite(
            Long userId, Long productId
    ) {
        return favoriteMapper.countByUserIdAndProductId(userId, productId) > 0;
    }


}
