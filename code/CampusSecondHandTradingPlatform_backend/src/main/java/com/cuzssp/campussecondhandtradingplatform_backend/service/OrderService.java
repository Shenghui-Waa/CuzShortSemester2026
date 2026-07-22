package com.cuzssp.campussecondhandtradingplatform_backend.service;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.CreateOrderRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.OrderVO;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.PageResult;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.Result;

public interface OrderService {

    Result<PageResult<OrderVO>> getOrders(Long userId, Integer status, Integer page, Integer pageSize);
    Result<OrderVO> getOrderDetail(Long userId, Long orderId);
    Result<OrderVO> createOrder(Long buyerId, CreateOrderRequest request);
    Result<Void> payOrder(Long userId, Long orderId);
    Result<Void> shipOrder(Long sellerId, Long orderId);
    Result<Void> confirmOrder(Long buyerId, Long orderId);
    Result<Void> cancelOrder(Long userId, Long orderId);
    // 管理员操作
    Result<PageResult<OrderVO>> getOrders(Integer page, Integer pageSize, Integer status);
}
