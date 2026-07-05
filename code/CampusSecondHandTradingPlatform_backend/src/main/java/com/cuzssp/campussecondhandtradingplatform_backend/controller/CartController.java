package com.cuzssp.campussecondhandtradingplatform_backend.controller;

import com.cuzssp.campussecondhandtradingplatform_backend.common.security.JwtTokenProvider;

import com.cuzssp.campussecondhandtradingplatform_backend.service.CartService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public Result<?> getCart(@RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        return cartService.getCart(userId);
    }

    @PostMapping
    public Result<?> addToCart(@RequestHeader("Authorization") String token,
                                @RequestParam Long productId) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        return cartService.addToCart(userId, productId);
    }

    @DeleteMapping("/{id}")
    public Result<?> removeFromCart(@RequestHeader("Authorization") String token,
                                     @PathVariable Long id) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        return cartService.removeFromCart(userId, id);
    }
}
