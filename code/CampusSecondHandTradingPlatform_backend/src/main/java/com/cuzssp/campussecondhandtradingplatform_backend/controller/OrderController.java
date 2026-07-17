package com.cuzssp.campussecondhandtradingplatform_backend.controller;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.CreateOrderRequest;

import com.cuzssp.campussecondhandtradingplatform_backend.common.security.JwtTokenProvider;
import com.cuzssp.campussecondhandtradingplatform_backend.service.OrderService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 创建订单
     * @param token
     * @param request
     * @return
     */
    @PostMapping
    public Result<?> createOrder(
            @RequestHeader("Authorization") String token,
            @RequestBody CreateOrderRequest request
    ) {
        Long currentUserId = getCurrentUserId(token);
        return orderService.createOrder(currentUserId, request);
    }

    /**
     * 获取订单列表
     * @param token
     * @param status
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping
    public Result<?> getOrders(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        Long currentUserId = getCurrentUserId(token);
        return orderService.getOrders(currentUserId, status, page, pageSize);
    }

    /**
     * 获取订单详情
     * @param token
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<?> getOrderDetail(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        Long currentUserId = getCurrentUserId(token);
        return orderService.getOrderDetail(currentUserId, id);
    }

    /**
     * 支付订单，为合规，不做支付逻辑
     * @param token
     * @param id
     * @return
     */
    @PutMapping("/{id}/pay")
    public Result<?> payOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        Long currentUserId = getCurrentUserId(token);
        return orderService.payOrder(currentUserId, id);
    }

    /**
     * 发货
     * @param token
     * @param id
     * @return
     */
    @PutMapping("/{id}/ship")
    public Result<?> shipOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        Long currentUserId = getCurrentUserId(token);
        return orderService.shipOrder(currentUserId, id);
    }

    /**
     * 收货
     * @param token
     * @param id
     * @return
     */
    @PutMapping("/{id}/confirm")
    public Result<?> confirmOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        Long currentUserId = getCurrentUserId(token);
        return orderService.confirmOrder(currentUserId, id);
    }

    /**
     * 取消订单
     * @param token
     * @param id
     * @return
     */
    @PutMapping("/{id}/cancel")
    public Result<?> cancelOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        Long currentUserId = getCurrentUserId(token);
        return orderService.cancelOrder(currentUserId, id);
    }

    private Long getCurrentUserId(String token) {
        if (token == null || token.isEmpty())
            return null;
        try {
            return jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        } catch (Exception e) {
            return null;
        }
    }
}
