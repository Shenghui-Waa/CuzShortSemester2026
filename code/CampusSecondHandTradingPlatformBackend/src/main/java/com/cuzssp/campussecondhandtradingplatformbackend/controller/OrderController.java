package com.cuzssp.campussecondhandtradingplatformbackend.controller;

import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.CreateOrderRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.security.TokenProvider;
import com.cuzssp.campussecondhandtradingplatformbackend.service.OrderService;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final TokenProvider tokenProvider;

    /**
     * 创建订单
     */
    @PostMapping
    public Result<?> createOrder(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        Long currentUserId = tokenProvider.getUserId(token);
        return Result.success(orderService.createOrder(currentUserId, request));
    }

    /**
     * 获取订单列表
     */
    @GetMapping
    public Result<?> getOrders(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        Long currentUserId = tokenProvider.getUserId(token);
        return Result.success(orderService.getOrders(currentUserId, status, page, pageSize));
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public Result<?> getOrderDetail(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        Long currentUserId = tokenProvider.getUserId(token);
        return Result.success(orderService.getOrderDetail(currentUserId, id));
    }

    /**
     * 支付订单，暂不做支付逻辑
     */
    @PutMapping("/{id}/pay")
    public Result<?> payOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        Long currentUserId = tokenProvider.getUserId(token);
        return Result.success(orderService.payOrder(currentUserId, id));
    }

    /**
     * 发货
     */
    @PutMapping("/{id}/ship")
    public Result<?> shipOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        Long currentUserId = tokenProvider.getUserId(token);
        return Result.success(orderService.shipOrder(currentUserId, id));
    }

    /**
     * 收货
     */
    @PutMapping("/{id}/confirm")
    public Result<?> confirmOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        Long currentUserId = tokenProvider.getUserId(token);
        return Result.success(orderService.confirmOrder(currentUserId, id));
    }

    /**
     * 取消订单
     */
    @PutMapping("/{id}/cancel")
    public Result<?> cancelOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        Long currentUserId = tokenProvider.getUserId(token);
        return Result.success(orderService.cancelOrder(currentUserId, id));
    }

}
