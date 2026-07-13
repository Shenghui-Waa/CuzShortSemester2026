package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;

import com.cuzssp.campussecondhandtradingplatform_backend.common.constant.OrderInfoConstant;
import com.cuzssp.campussecondhandtradingplatform_backend.common.constant.ProductConstant;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.*;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToEntityUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToVOUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.*;
import com.cuzssp.campussecondhandtradingplatform_backend.service.OrderService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.*;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.CreateOrderRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.exception.BusinessException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired private OrderMapper orderMapper;
    @Autowired private OrderItemMapper orderItemMapper;
    @Autowired private ProductMapper productMapper;
    @Autowired private ProductImageMapper productImageMapper;
    @Autowired private UserMapper userMapper;

    /**
     * 创建订单
     * @param buyerId
     * @param request
     * @return
     */
    @Override
    public Result<OrderVO> createOrder(
            Long buyerId, CreateOrderRequest request
    ) {
        Product product = productMapper.selectById(request.getProductId());
        if (product == null)
            throw new BusinessException("Product not found");
        if (product.getStatus() != ProductConstant.STATUS_ON_SALE)
            throw new BusinessException("Product not available");
        product.setStatus(ProductConstant.STATUS_SOLD_OUT);
        OrderInfo order = ToEntityUtil.toOrderInfoEntity(buyerId, product, request.getRemark());
        orderMapper.insert(order);
        OrderItem orderItem = ToEntityUtil.toOrderItemEntity(order, product);
        orderItemMapper.insert(orderItem);
        productMapper.updateById(product);
        return Result.success(toVO(orderMapper.selectById(order.getId())));
    }

    /**
     * 获取订单列表
     * @param userId
     * @param status
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Result<PageResult<OrderVO>> getOrders(
            Long userId, Integer status, Integer page, Integer pageSize
    ) {
        PageHelper.startPage(page, pageSize);
        List<OrderInfo> all = orderMapper.selectAll().stream()
                .filter(
                        order -> order.getBuyerId().equals(userId)
                                || order.getSellerId().equals(userId)
                ).collect(Collectors.toList());
        if (status != null)
            all = all.stream()
                    .filter(order -> order.getStatus().equals(status))
                    .collect(Collectors.toList());
        PageInfo<OrderInfo> orderPageInfo = new PageInfo<>(all);
        List<OrderVO> orderVOs = all.stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return Result.success(
                new PageResult<>(
                        orderVOs,
                        orderPageInfo.getTotal(),
                        orderPageInfo.getPageNum(),
                        orderPageInfo.getPageSize()
                )
        );
    }

    /**
     * 获取订单详情
     * @param userId
     * @param orderId
     * @return
     */
    @Override
    public Result<OrderVO> getOrderDetail(
            Long userId, Long orderId
    ) {
        OrderInfo orderInfo = orderMapper.selectById(orderId);
        if (orderInfo == null)
            throw new BusinessException("Order not found");
        return Result.success(toVO(orderInfo));
    }

    /**
     * 支付订单，为合规，不做支付逻辑
     * @param userId
     * @param orderId
     * @return
     */
    @Override
    public Result<Void> payOrder(
            Long userId, Long orderId
    ) {
        OrderInfo order = orderMapper.selectById(orderId);
        if (order == null)
            throw new BusinessException("Order not found");
        order.setStatus(OrderInfoConstant.STATUS_WAIT_DELIVER);
        orderMapper.updateById(order);
        return Result.success();
    }

    /**
     * 发货
     * @param sellerId
     * @param orderId
     * @return
     */
    @Override
    public Result<Void> shipOrder(
            Long sellerId, Long orderId
    ) {
        OrderInfo order = orderMapper.selectById(orderId);
        if (order == null)
            throw new BusinessException("Order not found");
        order.setStatus(OrderInfoConstant.STATUS_WAIT_RECEIVE);
        orderMapper.updateById(order);
        return Result.success();
    }

    /**
     * 收货
     * @param buyerId
     * @param orderId
     * @return
     */
    @Override
    public Result<Void> confirmOrder(Long buyerId, Long orderId) {
        OrderInfo order = orderMapper.selectById(orderId);
        if (order == null)
            throw new BusinessException("Order not found");
        order.setStatus(OrderInfoConstant.STATUS_COMPLETED);
        orderMapper.updateById(order);
        return Result.success();
    }

    @Override
    public Result<Void> cancelOrder(Long userId, Long orderId) {
        OrderInfo order = orderMapper.selectById(orderId);
        if (order == null)
            throw new BusinessException("Order not found");
        order.setStatus(OrderInfoConstant.STATUS_CANCELLED);
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem orderItem : orderItems) {
            Product product = productMapper.selectById(orderItem.getProductId());
            if (product != null) {
                product.setStatus(ProductConstant.STATUS_ON_SALE);
                productMapper.updateById(product);
            }
        }
        orderMapper.updateById(order);
        return Result.success();
    }

    private OrderVO toVO(OrderInfo orderInfo) {
        OrderVO orderVO = ToVOUtil.toOrderVO(
                orderInfo,
                userMapper.selectById(orderInfo.getBuyerId()),
                userMapper.selectById(orderInfo.getSellerId())
        );
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderInfo.getId());
        orderVO.setItems(orderItems.stream()
                .map(orderItem -> ToVOUtil.toOrderItemVO(
                        orderItem,
                        productMapper.selectById(orderItem.getProductId()),
                        productImageMapper
                ))
                .collect(Collectors.toList())
        );
        return orderVO;
    }
}
