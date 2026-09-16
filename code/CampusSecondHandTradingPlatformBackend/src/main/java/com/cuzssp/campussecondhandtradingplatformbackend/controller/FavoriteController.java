package com.cuzssp.campussecondhandtradingplatformbackend.controller;

import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.FavoriteRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.security.TokenProvider;
import jakarta.validation.Valid;
import com.cuzssp.campussecondhandtradingplatformbackend.service.FavoriteService;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final TokenProvider tokenProvider;

    /**
     * 获取收藏列表
     */
    @GetMapping
    public Result<?> getFavorites(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer pageSize
    ) {
        Long currentUserId = tokenProvider.getUserId(token);
        return Result.success(favoriteService.getFavorites(currentUserId, page, pageSize));
    }

    /**
     * 收藏
     */
    @PostMapping
    public Result<?> addFavorite(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody FavoriteRequest request
    ) {
        Long currentUserId = tokenProvider.getUserId(token);
        return Result.success(favoriteService.addFavorite(currentUserId, request));
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{productId}")
    public Result<?> removeFavorite(
            @RequestHeader("Authorization") String token,
            @PathVariable Long productId
    ) {
        Long currentUserId = tokenProvider.getUserId(token);
        return Result.success(favoriteService.removeFavorite(currentUserId, productId));
    }

    /**
     * 校验是否收藏
     */
    @GetMapping("/check/{productId}")
    public Result<?> checkFavorited(
            @RequestHeader("Authorization") String token,
            @PathVariable Long productId
    ) {
        Long currentUserId = tokenProvider.getUserId(token);
        return Result.success(favoriteService.isFavorited(currentUserId, productId));
    }

}
