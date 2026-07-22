package com.cuzssp.campussecondhandtradingplatform_backend.common.util;

import com.cuzssp.campussecondhandtradingplatform_backend.common.constant.OrderInfoConstant;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.*;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.*;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.OrderMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.ProductImageMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.ProductMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.UserMapper;
import java.math.BigDecimal;
import java.util.List;

public class ToVOUtil {

    // 用户 VO
    public static UserVO toUserVO(
            User user
    ) {
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setNickname(user.getNickname());
        userVO.setAvatar(user.getAvatar());
        userVO.setPhone(user.getPhone());
        userVO.setEmail(user.getEmail());
        userVO.setSchool(user.getSchool());
        userVO.setCampus(user.getCampus());
        userVO.setRole(user.getRole());
        userVO.setStatus(user.getStatus());
        userVO.setCreditScore(user.getCreditScore());
        userVO.setCreatedAt(user.getCreatedAt());
        return userVO;
    }

    // 仪表盘 VO
    public static DashboardVO toDashboardVO(
            UserMapper userMapper, ProductMapper productMapper, OrderMapper orderMapper
    ) {
        DashboardVO dashboardVO = new DashboardVO();
        dashboardVO.setUserCount((long) userMapper.countAll());
        dashboardVO.setProductCount((long) productMapper.countAll());
        dashboardVO.setOrderCount((long) orderMapper.countAll());
        dashboardVO.setTotalAmount(orderMapper.selectAll().stream()
                .filter(orderInfo -> orderInfo.getStatus() >= OrderInfoConstant.STATUS_WAIT_RECEIVE)
                .map(OrderInfo::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        dashboardVO.setTodayNewUsers((long) userMapper.countTodayNew());
        dashboardVO.setTodayNewOrders((long) orderMapper.countTodayNew());

        return dashboardVO;
    }

    // 订单物品 VO
    public static OrderItemVO toOrderItemVO(
            OrderItem orderItem, Product product, ProductImageMapper productImageMapper
    ){
        String productTitle = "";
        String productImage = "";
        if (product != null) {
            productTitle = product.getTitle();
            List<ProductImage> images = productImageMapper.selectByProductId(product.getId());
            productImage = images.isEmpty() ? null : images.get(0).getUrl();
        }
        return new OrderItemVO(
                orderItem.getId(),
                orderItem.getProductId(),
                productTitle,
                productImage,
                orderItem.getPrice()
        );
    }

    // 订单 VO
    public static OrderVO toOrderVO(
            OrderInfo orderInfo, User buyer, User seller
    ) {
        OrderVO orderVO = new OrderVO();
        orderVO.setId(orderInfo.getId());
        orderVO.setOrderNo(orderInfo.getOrderNo());
        orderVO.setBuyerId(orderInfo.getBuyerId());
        if (buyer != null)
            orderVO.setBuyerName(buyer.getNickname());
        orderVO.setSellerId(orderInfo.getSellerId());
        if (seller != null)
            orderVO.setSellerName(seller.getNickname());
        orderVO.setTotalAmount(orderInfo.getTotalAmount());
        orderVO.setStatus(orderInfo.getStatus());
        orderVO.setRemark(orderInfo.getRemark());

        orderVO.setCreatedAt(orderInfo.getCreatedAt());
        orderVO.setPaidAt(orderInfo.getPaidAt());
        orderVO.setShippedAt(orderInfo.getShippedAt());
        orderVO.setCompletedAt(orderInfo.getCompletedAt());

        return orderVO;
    }

    // 商品 VO
    public static ProductVO toProductVO(
            Product product, User seller, Category category
    ) {
        ProductVO productVO = new ProductVO();

        productVO.setId(product.getId());
        productVO.setUserId(product.getUserId());
        if (seller != null) {
            productVO.setSellerName(seller.getNickname());
            productVO.setSellerAvatar(seller.getAvatar());
        }
        productVO.setCategoryId(product.getCategoryId());
        if (category != null)
            productVO.setCategoryName(category.getName());
        productVO.setTitle(product.getTitle());
        productVO.setDescription(product.getDescription());
        productVO.setPrice(product.getPrice());
        productVO.setOriginalPrice(product.getOriginalPrice());
        productVO.setCondition(product.getCondition());
        productVO.setCampus(product.getCampus());
        productVO.setStatus(product.getStatus());
        productVO.setViewCount(product.getViewCount());

        productVO.setCreatedAt(product.getCreatedAt());
        productVO.setUpdatedAt(product.getUpdatedAt());

        return productVO;
    }

    // 购物车商品 VO
    public static CartItemVO toCartItemVO(
            CartItem cartItem, Product product, User seller, ProductImageMapper productImageMapper
    ) {
        CartItemVO cartItemVO = new CartItemVO();

        cartItemVO.setId(cartItem.getId());
        if (product != null) {
            cartItemVO.setProductId(product.getId());
            cartItemVO.setProductTitle(product.getTitle());
            List<ProductImage> images = productImageMapper.selectByProductId(product.getId());
            cartItemVO.setProductImage(images.isEmpty() ? null : images.get(0).getUrl());
            cartItemVO.setPrice(product.getPrice());
        }
        if (seller != null)
            cartItemVO.setSellerName(seller.getNickname());
        cartItemVO.setCreatedAt(cartItem.getCreatedAt());

        return cartItemVO;
    }

    //分类 VO
    public static CategoryVO toCategoryVO(
            Category category, long categoryId
    ) {
        return new CategoryVO(
                category.getId(),
                category.getName(),
                category.getIcon(),
                category.getSortOrder(),
                categoryId
        );
    }

    // 评论 VO
    public static ReviewVO toReviewVO(
            Review review, User reviewer
    ) {
        ReviewVO reviewVO = new ReviewVO();
        reviewVO.setId(review.getId());
        reviewVO.setOrderId(review.getOrderId());
        reviewVO.setReviewerId(review.getReviewerId());
        reviewVO.setTargetId(review.getTargetId());
        reviewVO.setRating(review.getRating());
        reviewVO.setContent(review.getContent());
        reviewVO.setCreatedAt(review.getCreatedAt());
        if (reviewer != null) {
            reviewVO.setReviewerName(reviewer.getNickname());
            reviewVO.setReviewerAvatar(reviewer.getAvatar());
        }
        return reviewVO;
    }

    // 聊天信息 VO
    public static ChatMessageVO toChatMessageVO(
            ChatMessage msg
    ) {
        ChatMessageVO chatMessageVO = new ChatMessageVO();
        chatMessageVO.setId(msg.getId());
        chatMessageVO.setSenderId(msg.getSenderId());
        chatMessageVO.setReceiverId(msg.getReceiverId());
        chatMessageVO.setProductId(msg.getProductId());
        chatMessageVO.setContent(msg.getContent());
        chatMessageVO.setIsRead(msg.getIsRead());
        chatMessageVO.setCreatedAt(msg.getCreatedAt());

        return chatMessageVO;
    }

    // 公告 VO
    public static AnnouncementVO toAnnouncementVO(Announcement announcement) {
        AnnouncementVO announcementVO = new AnnouncementVO();
        announcementVO.setId(announcement.getId());
        announcementVO.setTitle(announcement.getTitle());
        announcementVO.setContent(announcement.getContent());
        announcementVO.setCreatedAt(announcement.getCreatedAt());
        announcementVO.setUpdatedAt(announcement.getUpdatedAt());
        return announcementVO;

    }

}
