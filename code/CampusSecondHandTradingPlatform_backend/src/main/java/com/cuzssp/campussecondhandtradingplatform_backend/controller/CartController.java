package com.cuzssp.campussecondhandtradingplatform_backend.controller;

import com.cuzssp.campussecondhandtradingplatform_backend.common.security.JwtTokenProvider;

import com.cuzssp.campussecondhandtradingplatform_backend.service.CartService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 获取购物车
     * @param token
     * @return
     */
    @GetMapping
    public Result<?> getCart(
            @RequestHeader("Authorization") String token
    ) {
        Long currentUserId = getCurrentUserId(token);
        return cartService.getCart(currentUserId);
    }

    /**
     * 添加到购物车
     * @param token
     * @param productId
     * @return
     */
    @PostMapping
    public Result<?> addToCart(
            @RequestHeader("Authorization") String token,
            @RequestParam Long productId
    ) {
        Long currentUserId = getCurrentUserId(token);
        return cartService.addToCart(currentUserId, productId);
    }

    /**
     * 从购物车移除商品
     * @param token
     * @param productId
     * @return
     */
    @DeleteMapping("/{productId}")
    public Result<?> removeFromCart(
            @RequestHeader("Authorization") String token,
            @PathVariable Long productId
    ) {
        Long currentUserId = getCurrentUserId(token);
        return cartService.removeFromCart(currentUserId, productId);
    }

    private Long getCurrentUserId(String token) {
        if (token == null || token.isEmpty())
            return null;
        return jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
    }
}
