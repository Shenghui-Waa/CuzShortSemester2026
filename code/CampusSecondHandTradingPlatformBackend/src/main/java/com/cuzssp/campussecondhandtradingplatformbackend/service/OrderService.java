package com.cuzssp.campussecondhandtradingplatformbackend.service;

import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.OrderInfoRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.OrderVO;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.PageResult;

public interface OrderService {

    PageResult<OrderVO> getOrders(Long userId, Integer status, Integer page, Integer pageSize);
    OrderVO getOrderDetail(Long userId, Long orderId);
    OrderVO createOrder(Long buyerId, OrderInfoRequest request);
    Void payOrder(Long userId, Long orderId);
    Void shipOrder(Long sellerId, Long orderId);
    Void confirmOrder(Long buyerId, Long orderId);
    Void cancelOrder(Long userId, Long orderId);
    // 管理员操作
    PageResult<OrderVO> getOrders(Integer page, Integer pageSize, Integer status);
}
