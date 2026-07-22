package com.cuzssp.campussecondhandtradingplatform_backend.controller;

import com.cuzssp.campussecondhandtradingplatform_backend.common.security.SecurityUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.service.FavoriteService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final SecurityUtil securityUtil;

    /**
     * 获取收藏列表
     */
    @GetMapping
    public Result<?> getFavorites(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer pageSize
    ) {
        Long currentUserId = securityUtil.getCurrentUserId(token);
        return favoriteService.getFavorites(currentUserId, page, pageSize);
    }

    /**
     * 收藏
     */
    @PostMapping
    public Result<?> addFavorite(
            @RequestHeader("Authorization") String token,
            @RequestParam Long productId
    ) {
        Long currentUserId = securityUtil.getCurrentUserId(token);
        return favoriteService.addFavorite(currentUserId, productId);
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{productId}")
    public Result<?> removeFavorite(
            @RequestHeader("Authorization") String token,
            @PathVariable Long productId
    ) {
        Long currentUserId = securityUtil.getCurrentUserId(token);
        return favoriteService.removeFavorite(currentUserId, productId);
    }

    /**
     * 校验是否收藏
     */
    @GetMapping("/check/{productId}")
    public Result<?> checkFavorited(
            @RequestHeader("Authorization") String token,
            @PathVariable Long productId
    ) {
        Long currentUserId = securityUtil.getCurrentUserId(token);
        return favoriteService.isFavorited(currentUserId, productId);
    }

}
