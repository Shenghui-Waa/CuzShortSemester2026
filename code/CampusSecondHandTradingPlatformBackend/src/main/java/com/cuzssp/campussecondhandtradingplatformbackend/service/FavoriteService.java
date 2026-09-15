package com.cuzssp.campussecondhandtradingplatformbackend.service;

import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.PageResult;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.ProductVO;

public interface FavoriteService {

    PageResult<ProductVO> getFavorites(Long userId, Integer page, Integer pageSize);
    Void addFavorite(Long userId, Long productId);
    Void removeFavorite(Long userId, Long productId);
    Boolean isFavorited(Long userId, Long productId);

}
