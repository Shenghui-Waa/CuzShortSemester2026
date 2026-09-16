package com.cuzssp.campussecondhandtradingplatformbackend.controller;

import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.CartItemRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.security.TokenProvider;
import jakarta.validation.Valid;
import com.cuzssp.campussecondhandtradingplatformbackend.service.CartService;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final TokenProvider tokenProvider;

    /**
     * 获取购物车
     */
    @GetMapping
    public Result<?> getCart(
            @RequestHeader("Authorization") String token
    ) {
        Long currentUserId = tokenProvider.getUserId(token);
        return Result.success(cartService.getCart(currentUserId));
    }

    /**
     * 添加到购物车
     */
    @PostMapping
    public Result<?> addToCart(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody CartItemRequest request
    ) {
        Long currentUserId = tokenProvider.getUserId(token);
        return Result.success(cartService.addToCart(currentUserId, request));
    }

    /**
     * 从购物车移除商品
     */
    @DeleteMapping("/{productId}")
    public Result<?> removeFromCart(
            @RequestHeader("Authorization") String token,
            @PathVariable Long productId
    ) {
        Long currentUserId = tokenProvider.getUserId(token);
        return Result.success(cartService.removeFromCart(currentUserId, productId));
    }

}
