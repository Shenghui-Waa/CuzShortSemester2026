package com.cuzssp.campussecondhandtradingplatform_backend.controller;

import com.cuzssp.campussecondhandtradingplatform_backend.common.security.JwtTokenProvider;

import com.cuzssp.campussecondhandtradingplatform_backend.service.FavoriteService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public Result<?> getFavorites(@RequestHeader("Authorization") String token,
                                   @RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "12") Integer pageSize) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        return favoriteService.getFavorites(userId, page, pageSize);
    }

    @PostMapping
    public Result<?> addFavorite(@RequestHeader("Authorization") String token,
                                  @RequestParam Long productId) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        return favoriteService.addFavorite(userId, productId);
    }

    @DeleteMapping("/{productId}")
    public Result<?> removeFavorite(@RequestHeader("Authorization") String token,
                                     @PathVariable Long productId) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        return favoriteService.removeFavorite(userId, productId);
    }

    @GetMapping("/check/{productId}")
    public Result<?> checkFavorited(@RequestHeader("Authorization") String token,
                                     @PathVariable Long productId) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        return favoriteService.isFavorited(userId, productId);
    }
}
