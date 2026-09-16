package com.cuzssp.campussecondhandtradingplatformbackend.service.impl;

import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.OrderInfoConstant;
import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.ProductConstant;
import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.UserConstant;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.PageResult;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.Result;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.OrderInfo;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.OrderItem;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Product;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.ProductImage;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.ToEntityUtil;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.ToVOUtil;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.UtcTime;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.OrderInfoMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.OrderItemMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.CartItemMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.ProductImageMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.ProductMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.UserMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.service.OrderService;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.OrderVO;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.OrderInfoRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.OrderItemRequest;
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
    private final CartItemMapper cartItemMapper;

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
            Long buyerId, OrderInfoRequest request
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
                ProductConstant.Status.SOLD_OUT, UtcTime.now());
        if (reserved != 1)
            throw new BusinessException("Product is no longer available");

        OrderInfo order = ToEntityUtil.toOrderInfoEntity(buyerId, product, request);
        order.setUpdatedAt(UtcTime.now());
        orderMapper.insert(order);
        List<ProductImage> productImages = productImageMapper.selectByProductId(product.getId());
        String productImage = productImages.isEmpty() ? null : productImages.get(0).getUrl();
        OrderItemRequest orderItemRequest = new OrderItemRequest();
        orderItemRequest.setProductId(product.getId());
        orderItemRequest.setPrice(product.getPrice());
        orderItemRequest.setProductTitle(product.getTitle());
        orderItemRequest.setProductImage(productImage);
        orderItemRequest.setProductState(product.getState());
        OrderItem orderItem = ToEntityUtil.toOrderItemEntity(order, orderItemRequest);
        orderItemMapper.insert(orderItem);
        cartItemMapper.deleteByProductId(product.getId());
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
                UtcTime.now(), null, null);
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
                null, UtcTime.now(), null);
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
                null, null, UtcTime.now());
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

        int currentStatus = order.getStatus();
        if (currentStatus != OrderInfoConstant.Status.WAIT_PAY
                && currentStatus != OrderInfoConstant.Status.WAIT_DELIVER
                && currentStatus != OrderInfoConstant.Status.WAIT_RECEIVE)
            throw new BusinessException("Order cannot be cancelled in current status");

        boolean paid = currentStatus != OrderInfoConstant.Status.WAIT_PAY;
        int refundStatus = paid
                ? OrderInfoConstant.RefundStatus.REFUNDED
                : OrderInfoConstant.RefundStatus.NONE;
        LocalDateTime refundedAt = paid ? UtcTime.now() : null;
        int cancelled = orderMapper.cancelIfStatusMatches(
                orderId, currentStatus, refundStatus, refundedAt, UtcTime.now());
        if (cancelled != 1)
            throw new BusinessException("Order status has changed");

        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem orderItem : orderItems) {
            Product product = productMapper.selectById(orderItem.getProductId());
            if (product == null)
                throw new BusinessException("Product not found");

            int targetStatus = resolveCancellationProductStatus(currentStatus, product);
            int restored = productMapper.updateStatusIfMatches(
                    product.getId(), ProductConstant.Status.SOLD_OUT,
                    targetStatus, UtcTime.now());
            if (restored != 1)
                throw new BusinessException("Product status has changed");
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
                .map(ToVOUtil::toOrderItemVO)
                .collect(Collectors.toList())
        );
        return orderVO;
    }

    private int resolveCancellationProductStatus(int orderStatus, Product product) {
        if (orderStatus == OrderInfoConstant.Status.WAIT_RECEIVE)
            return ProductConstant.Status.DISABLE;

        User seller = userMapper.selectById(product.getUserId());
        if (seller == null || !Objects.equals(seller.getStatus(), UserConstant.Status.ACTIVE))
            return ProductConstant.Status.DISABLE;

        return ProductConstant.Status.ON_SALE;
    }

    private void transition(OrderInfo order, int nextStatus, LocalDateTime paidAt,
                            LocalDateTime shippedAt, LocalDateTime completedAt) {
        int updated = orderMapper.transitionIfStatusMatches(
                order.getId(), order.getStatus(), nextStatus, UtcTime.now(),
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
