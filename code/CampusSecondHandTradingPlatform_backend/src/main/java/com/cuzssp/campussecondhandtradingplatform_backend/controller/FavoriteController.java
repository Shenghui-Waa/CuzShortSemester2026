package com.cuzssp.campussecondhandtradingplatform_backend.controller;

import com.cuzssp.campussecondhandtradingplatform_backend.common.security.JwtTokenProvider;

import com.cuzssp.campussecondhandtradingplatform_backend.service.FavoriteService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired private FavoriteService favoriteService;
    @Autowired private JwtTokenProvider jwtTokenProvider;

    /**
     * 获取喜欢列表
     * @param token
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping
    public Result<?> getFavorites(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer pageSize
    ) {
        Long currentUserId = getCurrentUserId(token);
        return favoriteService.getFavorites(currentUserId, page, pageSize);
    }

    /**
     * 加喜欢
     * @param token
     * @param productId
     * @return
     */
    @PostMapping
    public Result<?> addFavorite(
            @RequestHeader("Authorization") String token,
            @RequestParam Long productId
    ) {
        Long currentUserId = getCurrentUserId(token);
        return favoriteService.addFavorite(currentUserId, productId);
    }

    /**
     * 移除喜欢
     * @param token
     * @param productId
     * @return
     */
    @DeleteMapping("/{productId}")
    public Result<?> removeFavorite(
            @RequestHeader("Authorization") String token,
            @PathVariable Long productId
    ) {
        Long currentUserId = getCurrentUserId(token);
        return favoriteService.removeFavorite(currentUserId, productId);
    }

    /**
     * 校验是否喜欢
     * @param token
     * @param productId
     * @return
     */
    @GetMapping("/check/{productId}")
    public Result<?> checkFavorited(
            @RequestHeader("Authorization") String token,
            @PathVariable Long productId
    ) {
        Long currentUserId = getCurrentUserId(token);
        return favoriteService.isFavorited(currentUserId, productId);
    }

    private Long getCurrentUserId(String token) {
        if (token == null || token.isEmpty())
            return null;
        return jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));

    }
}
