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
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final ProductImageMapper productImageMapper;
    private final UserMapper userMapper;

    /**
     * 创建订单
     * @param buyerId
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<OrderVO> createOrder(
            Long buyerId, CreateOrderRequest request
    ) {
        Product product = productMapper.selectByIdForUpdate(request.getProductId());
        if (product == null)
            throw new BusinessException("Product not found");
        if (product.getStatus() != ProductConstant.STATUS_ON_SALE)
            throw new BusinessException("Product not available");
        if (Objects.equals(buyerId, product.getUserId()))
            throw new BusinessException("Buyer cannot be seller");
        product.setStatus(ProductConstant.STATUS_SOLD_OUT);
        product.setUpdatedAt(LocalDateTime.now());
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
        List<OrderInfo> all = orderMapper.selectAllWithUserIdOrStatus(userId, status);
        return getPageResultResult(all);
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
            throw new BusinessException(404, "Order not found");

        return Result.success(toVO(orderInfo));
    }

    /**
     * 支付订单，为合规，不做支付逻辑
     * @param userId
     * @param orderId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> payOrder(
            Long userId, Long orderId
    ) {
        OrderInfo order = orderMapper.selectById(orderId);
        if (order == null)
            throw new BusinessException(404, "Order not found");
        if (!Objects.equals(order.getBuyerId(), userId))
            throw new BusinessException(403, "Permission denied");
        if (order.getStatus() != OrderInfoConstant.STATUS_WAIT_PAY)
            throw new BusinessException(400, "Invalid order status");
        order.setStatus(OrderInfoConstant.STATUS_WAIT_DELIVER);
        order.setPaidAt(LocalDateTime.now());
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
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> shipOrder(
            Long sellerId, Long orderId
    ) {
        OrderInfo order = orderMapper.selectById(orderId);
        if (order == null)
            throw new BusinessException(404, "Order not found");
        if (!Objects.equals(order.getSellerId(), sellerId))
            throw new BusinessException(403, "Permission denied");
        if (order.getStatus() != OrderInfoConstant.STATUS_WAIT_DELIVER)
            throw new BusinessException(400, "Invalid order status");
        order.setStatus(OrderInfoConstant.STATUS_WAIT_RECEIVE);
        order.setShippedAt(LocalDateTime.now());
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
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> confirmOrder(
            Long buyerId, Long orderId
    ) {
        OrderInfo order = orderMapper.selectById(orderId);
        if (order == null)
            throw new BusinessException(404, "Order not found");
        if (!Objects.equals(order.getBuyerId(), buyerId))
            throw new BusinessException(403, "Permission denied");
        if (order.getStatus() != OrderInfoConstant.STATUS_WAIT_RECEIVE)
            throw new BusinessException(400, "Invalid order status");
        order.setStatus(OrderInfoConstant.STATUS_COMPLETED);
        order.setCompletedAt(LocalDateTime.now());
        orderMapper.updateById(order);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> cancelOrder(
            Long userId, Long orderId
    ) {
        OrderInfo order = orderMapper.selectById(orderId);
        if (order == null)
            throw new BusinessException(404, "Order not found");
        if (!Objects.equals(order.getBuyerId(), userId))
            throw new BusinessException(403, "Permission denied");
        if (order.getStatus() == OrderInfoConstant.STATUS_COMPLETED
                || order.getStatus() == OrderInfoConstant.STATUS_CANCELLED)
            throw new BusinessException(400, "Order cannot be cancelled in current status");

        order.setStatus(OrderInfoConstant.STATUS_CANCELLED);
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem orderItem : orderItems) {
            Product product = productMapper.selectById(orderItem.getProductId());
            if (product != null) {
                product.setStatus(ProductConstant.STATUS_ON_SALE);
                product.setUpdatedAt(LocalDateTime.now());
                productMapper.updateById(product);
            }
        }
        orderMapper.updateById(order);
        return Result.success();
    }

    /*
    管理员操作
     */
    @Override
    public Result<PageResult<OrderVO>> getOrders(
            Integer page, Integer pageSize, @Nullable Integer status
    ) {
        PageHelper.startPage(page, pageSize);
        List<OrderInfo> all = orderMapper.selectAllWithUserIdOrStatus(null, status);
        return getPageResultResult(all);
    }

    /*
    内部私有方法
     */
    @NonNull
    private Result<PageResult<OrderVO>> getPageResultResult(
            List<OrderInfo> all
    ) {
        PageInfo<OrderInfo> orderPageInfo = new PageInfo<>(all);
        List<OrderVO> orderVOs = all.stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return Result.success(new PageResult<>(
                orderVOs, orderPageInfo.getTotal(),
                orderPageInfo.getPageNum(), orderPageInfo.getPageSize())
        );
    }

    private OrderVO toVO(
            OrderInfo orderInfo
    ) {
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
