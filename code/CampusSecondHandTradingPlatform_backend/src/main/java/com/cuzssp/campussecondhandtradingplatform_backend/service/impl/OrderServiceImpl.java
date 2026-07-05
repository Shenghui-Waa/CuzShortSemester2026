package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.*;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.*;
import com.cuzssp.campussecondhandtradingplatform_backend.service.OrderService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.*;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.CreateOrderRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.exception.BusinessException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired private OrderMapper orderMapper;
    @Autowired private OrderItemMapper orderItemMapper;
    @Autowired private ProductMapper productMapper;
    @Autowired private ProductImageMapper productImageMapper;
    @Autowired private UserMapper userMapper;

    @Override
    public Result<OrderVO> createOrder(Long buyerId, CreateOrderRequest request) {
        Product product = productMapper.selectById(request.getProductId());
        if (product == null) throw new BusinessException("Product not found");
        if (product.getStatus() != 0) throw new BusinessException("Product not available");
        product.setStatus(2);
        OrderInfo order = new OrderInfo();
        order.setOrderNo(UUID.randomUUID().toString().replace("-","").substring(0,20));
        order.setBuyerId(buyerId); order.setSellerId(product.getUserId());
        order.setTotalAmount(product.getPrice()); order.setStatus(0);
        order.setRemark(request.getRemark()); order.setCreatedAt(LocalDateTime.now());
        orderMapper.insert(order);
        OrderItem item = new OrderItem();
        item.setOrderId(order.getId()); item.setProductId(product.getId());
        item.setPrice(product.getPrice()); item.setCreatedAt(LocalDateTime.now());
        orderItemMapper.insert(item);
        productMapper.updateById(product);
        return Result.success(toVO(orderMapper.selectById(order.getId())));
    }

    @Override
    public Result<PageResult<OrderVO>> getOrders(Long userId, Integer status, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<OrderInfo> all = orderMapper.selectAll().stream()
                .filter(o -> o.getBuyerId().equals(userId) || o.getSellerId().equals(userId))
                .collect(Collectors.toList());
        if (status != null) all = all.stream().filter(o -> o.getStatus().equals(status)).collect(Collectors.toList());
        PageInfo<OrderInfo> pgInfo = new PageInfo<>(all);
        List<OrderVO> vos = all.stream().map(this::toVO).collect(Collectors.toList());
        return Result.success(new PageResult<>(vos, pgInfo.getTotal(), pgInfo.getPageNum(), pgInfo.getPageSize()));
    }

    @Override
    public Result<OrderVO> getOrderDetail(Long userId, Long orderId) {
        OrderInfo order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException("Order not found");
        return Result.success(toVO(order));
    }

    @Override
    public Result<Void> payOrder(Long userId, Long orderId) {
        OrderInfo order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException("Order not found");
        order.setStatus(2); orderMapper.updateById(order);
        return Result.success();
    }

    @Override
    public Result<Void> shipOrder(Long sellerId, Long orderId) {
        OrderInfo order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException("Order not found");
        order.setStatus(3); orderMapper.updateById(order);
        return Result.success();
    }

    @Override
    public Result<Void> confirmOrder(Long buyerId, Long orderId) {
        OrderInfo order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException("Order not found");
        order.setStatus(4); orderMapper.updateById(order);
        return Result.success();
    }

    @Override
    public Result<Void> cancelOrder(Long userId, Long orderId) {
        OrderInfo order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException("Order not found");
        order.setStatus(1);
        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem oi : items) {
            Product product = productMapper.selectById(oi.getProductId());
            if (product != null) { product.setStatus(0); productMapper.updateById(product); }
        }
        orderMapper.updateById(order);
        return Result.success();
    }

    private OrderVO toVO(OrderInfo order) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId()); vo.setOrderNo(order.getOrderNo()); vo.setBuyerId(order.getBuyerId());
        vo.setSellerId(order.getSellerId()); vo.setTotalAmount(order.getTotalAmount());
        vo.setStatus(order.getStatus()); vo.setRemark(order.getRemark());
        vo.setCreatedAt(order.getCreatedAt()); vo.setPaidAt(order.getPaidAt());
        vo.setShippedAt(order.getShippedAt()); vo.setCompletedAt(order.getCompletedAt());
        User buyer = userMapper.selectById(order.getBuyerId());
        if (buyer != null) vo.setBuyerName(buyer.getNickname());
        User seller = userMapper.selectById(order.getSellerId());
        if (seller != null) vo.setSellerName(seller.getNickname());
        List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
        vo.setItems(items.stream().map(item -> {
            OrderItemVO ivo = new OrderItemVO();
            ivo.setId(item.getId()); ivo.setProductId(item.getProductId()); ivo.setPrice(item.getPrice());
            Product p = productMapper.selectById(item.getProductId());
            if (p != null) {
                ivo.setProductTitle(p.getTitle());
                List<ProductImage> imgs = productImageMapper.selectByProductId(p.getId());
                ivo.setProductImage(imgs.isEmpty() ? null : imgs.get(0).getUrl());
            }
            return ivo;
        }).collect(Collectors.toList()));
        return vo;
    }
}
