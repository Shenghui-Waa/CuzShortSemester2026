package com.cuzssp.campussecondhandtradingplatform_backend.service;

import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.PageResult;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.ProductVO;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;

public interface FavoriteService {
    Result<Void> addFavorite(Long userId, Long productId);
    Result<Void> removeFavorite(Long userId, Long productId);
    Result<Boolean> isFavorited(Long userId, Long productId);
    Result<PageResult<ProductVO>> getFavorites(Long userId, Integer page, Integer pageSize);
}
