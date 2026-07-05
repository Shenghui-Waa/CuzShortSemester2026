package com.cuzssp.campussecondhandtradingplatform_backend.controller;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.CreateOrderRequest;

import com.cuzssp.campussecondhandtradingplatform_backend.common.security.JwtTokenProvider;
import com.cuzssp.campussecondhandtradingplatform_backend.service.OrderService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public Result<?> createOrder(@RequestHeader("Authorization") String token,
                                  @RequestBody CreateOrderRequest request) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        return orderService.createOrder(userId, request);
    }

    @GetMapping
    public Result<?> getOrders(@RequestHeader("Authorization") String token,
                               @RequestParam(required = false) Integer status,
                               @RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        return orderService.getOrders(userId, status, page, pageSize);
    }

    @GetMapping("/{id}")
    public Result<?> getOrderDetail(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        return orderService.getOrderDetail(userId, id);
    }

    @PutMapping("/{id}/pay")
    public Result<?> payOrder(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        return orderService.payOrder(userId, id);
    }

    @PutMapping("/{id}/ship")
    public Result<?> shipOrder(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        return orderService.shipOrder(userId, id);
    }

    @PutMapping("/{id}/confirm")
    public Result<?> confirmOrder(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        return orderService.confirmOrder(userId, id);
    }

    @PutMapping("/{id}/cancel")
    public Result<?> cancelOrder(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        return orderService.cancelOrder(userId, id);
    }
}
