package com.cuzssp.campussecondhandtradingplatformbackend.service.impl;

import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.OrderInfoConstant;
import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.ProductConstant;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.PageResult;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.Result;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.OrderInfo;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.OrderItem;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Product;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.ToEntityUtil;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.ToVOUtil;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.OrderInfoMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.OrderItemMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.ProductImageMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.ProductMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.UserMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.service.OrderService;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.OrderVO;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.CreateOrderRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.exception.BusinessException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderInfoMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final ProductImageMapper productImageMapper;
    private final UserMapper userMapper;

    // 获取订单列表
    @Override
    public PageResult<OrderVO> getOrders(
            Long userId, Integer status, Integer page, Integer pageSize
    ) {
        validatePagination(page, pageSize);
        PageHelper.startPage(page, pageSize);
        try {
            List<OrderInfo> all = orderMapper.selectByUserIdOrStatus(userId, status);
            return getPageResultResult(all);
        } finally {
            PageHelper.clearPage();
        }
    }

    // 获取订单详情
    @Override
    public OrderVO getOrderDetail(
            Long userId, Long orderId
    ) {
        OrderInfo orderInfo = orderMapper.selectById(orderId);
        if (orderInfo == null)
            throw new BusinessException(Result.Code.NOT_FOUND, "Order not found");

        if (!Objects.equals(orderInfo.getBuyerId(), userId)
                && !Objects.equals(orderInfo.getSellerId(), userId))
            throw new BusinessException(Result.Code.FORBIDDEN, "Permission denied");

        return toVO(orderInfo);
    }

    // 创建订单
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO createOrder(
            Long buyerId, CreateOrderRequest request
    ) {
        if (request == null || request.getProductId() == null)
            throw new BusinessException("Product ID is required");

        Product product = productMapper.selectById(request.getProductId());
        if (product == null)
            throw new BusinessException(Result.Code.NOT_FOUND, "Product not found");

        if (product.getStatus() != ProductConstant.Status.ON_SALE)
            throw new BusinessException("Product not available");

        if (Objects.equals(buyerId, product.getUserId()))
            throw new BusinessException("Buyer cannot be seller");

        // Conditional write is portable and reserves a single second-hand item atomically.
        int reserved = productMapper.reserveIfAvailable(
                product.getId(), ProductConstant.Status.ON_SALE, product.getPrice(),
                ProductConstant.Status.SOLD_OUT, LocalDateTime.now());
        if (reserved != 1)
            throw new BusinessException("Product is no longer available");

        OrderInfo order = ToEntityUtil.toOrderInfoEntity(buyerId, product, request.getRemark());
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.insert(order);
        OrderItem orderItem = ToEntityUtil.toOrderItemEntity(order, product);
        orderItemMapper.insert(orderItem);
        return toVO(orderMapper.selectById(order.getId()));
    }



    // 支付订单，暂不做支付逻辑
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void payOrder(
            Long userId, Long orderId
    ) {
        OrderInfo order = orderMapper.selectById(orderId);
        if (order == null)
            throw new BusinessException(Result.Code.NOT_FOUND, "Order not found");

        if (!Objects.equals(order.getBuyerId(), userId))
            throw new BusinessException(Result.Code.FORBIDDEN, "Permission denied");

        if (order.getStatus() != OrderInfoConstant.Status.WAIT_PAY)
            throw new BusinessException("Invalid order status");

        transition(order, OrderInfoConstant.Status.WAIT_DELIVER,
                LocalDateTime.now(), null, null);
        return null;
    }

    // 发货
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void shipOrder(
            Long sellerId, Long orderId
    ) {
        OrderInfo order = orderMapper.selectById(orderId);
        if (order == null)
            throw new BusinessException(Result.Code.NOT_FOUND, "Order not found");

        if (!Objects.equals(order.getSellerId(), sellerId))
            throw new BusinessException(Result.Code.FORBIDDEN, "Permission denied");

        if (order.getStatus() != OrderInfoConstant.Status.WAIT_DELIVER)
            throw new BusinessException("Invalid order status");

        transition(order, OrderInfoConstant.Status.WAIT_RECEIVE,
                null, LocalDateTime.now(), null);
        return null;
    }

    // 收货
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void confirmOrder(
            Long buyerId, Long orderId
    ) {
        OrderInfo order = orderMapper.selectById(orderId);
        if (order == null)
            throw new BusinessException(Result.Code.NOT_FOUND, "Order not found");

        if (!Objects.equals(order.getBuyerId(), buyerId))
            throw new BusinessException(Result.Code.FORBIDDEN, "Permission denied");

        if (order.getStatus() != OrderInfoConstant.Status.WAIT_RECEIVE)
            throw new BusinessException("Invalid order status");

        transition(order, OrderInfoConstant.Status.COMPLETED,
                null, null, LocalDateTime.now());
        return null;
    }

    // 取消订单
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void cancelOrder(
            Long userId, Long orderId
    ) {
        OrderInfo order = orderMapper.selectById(orderId);
        if (order == null)
            throw new BusinessException(Result.Code.NOT_FOUND, "Order not found");

        if (!Objects.equals(order.getBuyerId(), userId))
            throw new BusinessException(Result.Code.FORBIDDEN, "Permission denied");

        if (order.getStatus() == OrderInfoConstant.Status.COMPLETED
                || order.getStatus() == OrderInfoConstant.Status.CANCELLED)
            throw new BusinessException("Order cannot be cancelled in current status");


        transition(order, OrderInfoConstant.Status.CANCELLED, null, null, null);
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem orderItem : orderItems) {
            Product product = productMapper.selectById(orderItem.getProductId());
            if (product != null) {
                productMapper.updateStatusIfMatches(
                        product.getId(), ProductConstant.Status.SOLD_OUT,
                        ProductConstant.Status.ON_SALE, LocalDateTime.now());
            }
        }
        return null;
    }


    // =====================================================================================
    // ===========================>>>>> 管 理 员 操 作 <<<<<==================================
    // =====================================================================================

    // 获取订单
    @Override
    public PageResult<OrderVO> getOrders(
            Integer page, Integer pageSize, @Nullable Integer status
    ) {
        validatePagination(page, pageSize);
        PageHelper.startPage(page, pageSize);
        try {
            List<OrderInfo> all = orderMapper.selectByUserIdOrStatus(null, status);
            return getPageResultResult(all);
        } finally {
            PageHelper.clearPage();
        }
    }

    // #===========>>>>> 内 部 私 有 工 具 方 法 <<<<<===========#

    @NonNull
    private PageResult<OrderVO> getPageResultResult(
            List<OrderInfo> all
    ) {
        PageInfo<OrderInfo> orderPageInfo = new PageInfo<>(all);
        List<OrderVO> orderVOs = all
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return new PageResult<>(
                orderVOs, orderPageInfo.getTotal(),
                orderPageInfo.getPageNum(), orderPageInfo.getPageSize());
    }

    private OrderVO toVO(
            OrderInfo orderInfo
    ) {
        OrderVO orderVO = ToVOUtil.toOrderVO(
                orderInfo, userMapper.selectById(orderInfo.getBuyerId()),
                userMapper.selectById(orderInfo.getSellerId())
        );
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderInfo.getId());
        orderVO.setItems(orderItems
                .stream()
                .map(orderItem -> ToVOUtil.toOrderItemVO(
                        orderItem, productMapper.selectById(orderItem.getProductId()),
                        productImageMapper.selectByProductId(orderItem.getProductId())
                ))
                .collect(Collectors.toList())
        );
        return orderVO;
    }

    private void transition(OrderInfo order, int nextStatus, LocalDateTime paidAt,
                            LocalDateTime shippedAt, LocalDateTime completedAt) {
        int updated = orderMapper.transitionIfStatusMatches(
                order.getId(), order.getStatus(), nextStatus, LocalDateTime.now(),
                paidAt, shippedAt, completedAt);
        if (updated != 1) {
            throw new BusinessException("Order status has changed");
        }
    }

    private void validatePagination(Integer page, Integer pageSize) {
        if (page == null || page < 1 || pageSize == null || pageSize < 1 || pageSize > 100) {
            throw new BusinessException("Invalid pagination");
        }
    }

}
