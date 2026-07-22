package com.cuzssp.campussecondhandtradingplatform_backend.controller;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.CreateOrderRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.security.SecurityUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.service.OrderService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final SecurityUtil securityUtil;

    /**
     * 创建订单
     */
    @PostMapping
    public Result<?> createOrder(
            @RequestHeader("Authorization") String token,
            @RequestBody CreateOrderRequest request
    ) {
        Long currentUserId = securityUtil.getCurrentUserId(token);
        return orderService.createOrder(currentUserId, request);
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
        Long currentUserId = securityUtil.getCurrentUserId(token);
        return orderService.getOrders(currentUserId, status, page, pageSize);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public Result<?> getOrderDetail(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        Long currentUserId = securityUtil.getCurrentUserId(token);
        return orderService.getOrderDetail(currentUserId, id);
    }

    /**
     * 支付订单，暂不做支付逻辑
     */
    @PutMapping("/{id}/pay")
    public Result<?> payOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        Long currentUserId = securityUtil.getCurrentUserId(token);
        return orderService.payOrder(currentUserId, id);
    }

    /**
     * 发货
     */
    @PutMapping("/{id}/ship")
    public Result<?> shipOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        Long currentUserId = securityUtil.getCurrentUserId(token);
        return orderService.shipOrder(currentUserId, id);
    }

    /**
     * 收货
     */
    @PutMapping("/{id}/confirm")
    public Result<?> confirmOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        Long currentUserId = securityUtil.getCurrentUserId(token);
        return orderService.confirmOrder(currentUserId, id);
    }

    /**
     * 取消订单
     */
    @PutMapping("/{id}/cancel")
    public Result<?> cancelOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        Long currentUserId = securityUtil.getCurrentUserId(token);
        return orderService.cancelOrder(currentUserId, id);
    }

}
