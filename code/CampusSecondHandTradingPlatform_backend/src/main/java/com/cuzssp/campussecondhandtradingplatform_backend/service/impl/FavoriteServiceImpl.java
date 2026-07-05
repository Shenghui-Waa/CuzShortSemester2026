package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.*;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.*;
import com.cuzssp.campussecondhandtradingplatform_backend.service.FavoriteService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {
    @Autowired private FavoriteMapper favoriteMapper;
    @Autowired private ProductMapper productMapper;
    @Autowired private ProductImageMapper productImageMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private CategoryMapper categoryMapper;

    @Override
    public Result<Void> addFavorite(Long userId, Long productId) {
        if (favoriteMapper.countByUserIdAndProductId(userId, productId) > 0) return Result.success();
        Favorite fav = new Favorite(); fav.setUserId(userId); fav.setProductId(productId);
        favoriteMapper.insert(fav);
        return Result.success();
    }

    @Override
    public Result<Void> removeFavorite(Long userId, Long productId) {
        favoriteMapper.deleteByUserIdAndProductId(userId, productId);
        return Result.success();
    }

    @Override
    public Result<Boolean> isFavorited(Long userId, Long productId) {
        return Result.success(favoriteMapper.countByUserIdAndProductId(userId, productId) > 0);
    }

    @Override
    public Result<PageResult<ProductVO>> getFavorites(Long userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<Favorite> pg = favoriteMapper.selectByUserId(userId);
        PageInfo<Favorite> pgInfo = new PageInfo<>(pg);
        List<ProductVO> vos = pg.stream().map(fav -> {
            Product p = productMapper.selectById(fav.getProductId());
            if (p == null) return null;
            ProductVO vo = new ProductVO();
            vo.setId(p.getId()); vo.setUserId(p.getUserId()); vo.setCategoryId(p.getCategoryId());
            vo.setTitle(p.getTitle()); vo.setPrice(p.getPrice()); vo.setOriginalPrice(p.getOriginalPrice());
            vo.setCondition(p.getCondition()); vo.setCampus(p.getCampus()); vo.setStatus(p.getStatus());
            vo.setCreatedAt(p.getCreatedAt()); vo.setIsFavorited(true);
            User seller = userMapper.selectById(p.getUserId());
            if (seller != null) vo.setSellerName(seller.getNickname());
            Category cat = categoryMapper.selectById(p.getCategoryId());
            if (cat != null) vo.setCategoryName(cat.getName());
            vo.setImages(productImageMapper.selectByProductId(p.getId()).stream()
                    .map(ProductImage::getUrl).collect(Collectors.toList()));
            return vo;
        }).filter(v -> v != null).collect(Collectors.toList());
        return Result.success(new PageResult<>(vos, pgInfo.getTotal(), pgInfo.getPageNum(), pgInfo.getPageSize()));
    }
}
