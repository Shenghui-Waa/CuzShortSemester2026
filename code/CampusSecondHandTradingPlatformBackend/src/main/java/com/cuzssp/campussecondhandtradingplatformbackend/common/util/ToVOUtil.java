package com.cuzssp.campussecondhandtradingplatformbackend.common.util;


import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.*;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ToVOUtil {

    // 用户 VO
    public static UserVO toUserVO(
            User user
    ) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setSchool(user.getSchool());
        vo.setCampus(user.getCampus());
        vo.setRole(user.getRole());
        vo.setStatus(user.getStatus());
        vo.setCreditScore(user.getCreditScore());
        vo.setCreatedAt(user.getCreatedAt());
        return vo;
    }

    // 仪表盘 VO
    public static DashboardVO toDashboardVO(
            Long userCount, Long newUserCount, Long productCount,
            Long orderCount, Long newOrderCount, BigDecimal totalAmount
    ) {
        DashboardVO vo = new DashboardVO();
        vo.setUserCount(userCount);
        vo.setTodayNewUsers(newUserCount);
        vo.setProductCount(productCount);
        vo.setOrderCount(orderCount);
        vo.setTodayNewOrders(newOrderCount);
        vo.setTotalAmount(totalAmount);
        return vo;
    }

    // 订单物品 VO
    public static OrderItemVO toOrderItemVO(
            OrderItem orderItem
    ){
        OrderItemVO vo = new OrderItemVO();
        vo.setId(orderItem.getId());
        vo.setProductId(orderItem.getProductId());
        vo.setProductTitle(orderItem.getProductTitle());
        vo.setProductImage(orderItem.getProductImage());
        vo.setPrice(orderItem.getPrice());
        vo.setProductState(orderItem.getProductState());

        return vo;
    }

    // 订单 VO
    public static OrderVO toOrderVO(
            OrderInfo orderInfo, User buyer, User seller
    ) {
        OrderVO vo = new OrderVO();
        vo.setId(orderInfo.getId());
        vo.setOrderNo(orderInfo.getOrderNo());
        vo.setBuyerId(orderInfo.getBuyerId());
        if (buyer != null)
            vo.setBuyerName(buyer.getNickname());
        vo.setSellerId(orderInfo.getSellerId());
        if (seller != null)
            vo.setSellerName(seller.getNickname());
        vo.setTotalAmount(orderInfo.getTotalAmount());
        vo.setStatus(orderInfo.getStatus());
        vo.setRemark(orderInfo.getRemark());

        vo.setCreatedAt(orderInfo.getCreatedAt());
        vo.setPaidAt(orderInfo.getPaidAt());
        vo.setShippedAt(orderInfo.getShippedAt());
        vo.setCompletedAt(orderInfo.getCompletedAt());
        vo.setRefundStatus(orderInfo.getRefundStatus());
        vo.setRefundedAt(orderInfo.getRefundedAt());

        return vo;
    }

    // 商品 VO
    public static ProductVO toProductVO(
            Product product, User seller, Category category,
            List<ProductImage> productImages
    ) {
        ProductVO vo = new ProductVO();
        vo.setId(product.getId());
        vo.setUserId(product.getUserId());
        if (seller != null) {
            vo.setSellerName(seller.getNickname());
            vo.setSellerAvatar(seller.getAvatar());
        }
        vo.setCategoryId(product.getCategoryId());
        if (category != null)
            vo.setCategoryName(category.getName());
        vo.setTitle(product.getTitle());
        vo.setDescription(product.getDescription());
        vo.setPrice(product.getPrice());
        vo.setOriginalPrice(product.getOriginalPrice());
        vo.setState(product.getState());
        vo.setCampus(product.getCampus());
        vo.setStatus(product.getStatus());
        vo.setViewCount(product.getViewCount());
        List<String> images = new ArrayList<>();
        productImages.forEach(image -> images.add(image.getUrl()));
        vo.setImages(images);
        // isFavorite 单独设置
        vo.setCreatedAt(product.getCreatedAt());
        vo.setUpdatedAt(product.getUpdatedAt());
        return vo;
    }

    // 购物车商品 VO
    public static CartItemVO toCartItemVO(
            CartItem cartItem, Product product, User seller,
            List<ProductImage> productImages
    ) {
        CartItemVO vo = new CartItemVO();
        vo.setId(cartItem.getId());
        if (product != null) {
            vo.setProductId(product.getId());
            vo.setProductTitle(product.getTitle());
            vo.setProductImage(productImages.isEmpty()
                    ? null
                    : productImages.get(0).getUrl());
            vo.setPrice(product.getPrice());
        }
        if (seller != null)
            vo.setSellerName(seller.getNickname());
        vo.setCreatedAt(cartItem.getCreatedAt());
        return vo;
    }

    //分类 VO
    public static CategoryVO toCategoryVO(
            Category category, Long productCount
    ) {
        CategoryVO vo = new CategoryVO();
        vo.setId(category.getId());
        vo.setName(category.getName());
        vo.setIcon(category.getIcon());
        vo.setSortOrder(category.getSortOrder());
        vo.setProductCount(productCount);
        return vo;
    }

    // 评论 VO
    public static ReviewVO toReviewVO(
            Review review, User reviewer
    ) {
        ReviewVO vo = new ReviewVO();
        vo.setId(review.getId());
        vo.setOrderId(review.getOrderId());
        vo.setReviewerId(review.getReviewerId());
        vo.setTargetId(review.getTargetId());
        vo.setRating(review.getRating());
        vo.setContent(review.getContent());
        vo.setCreatedAt(review.getCreatedAt());
        if (reviewer != null) {
            vo.setReviewerName(reviewer.getNickname());
            vo.setReviewerAvatar(reviewer.getAvatar());
        }
        return vo;
    }

    // 聊天信息 VO
    public static ChatMessageVO toChatMessageVO(
            ChatMessage msg
    ) {
        ChatMessageVO vo = new ChatMessageVO();
        vo.setId(msg.getId());
        vo.setSenderId(msg.getSenderId());
        vo.setReceiverId(msg.getReceiverId());
        vo.setProductId(msg.getProductId());
        vo.setContent(msg.getContent());
        vo.setIsRead(msg.getIsRead());
        vo.setCreatedAt(msg.getCreatedAt());

        return vo;
    }

    // 公告 VO
    public static AnnouncementVO toAnnouncementVO(
            Announcement announcement
    ) {
        AnnouncementVO vo = new AnnouncementVO();
        vo.setId(announcement.getId());
        vo.setTitle(announcement.getTitle());
        vo.setContent(announcement.getContent());
        vo.setCreatedAt(announcement.getCreatedAt());
        vo.setUpdatedAt(announcement.getUpdatedAt());
        return vo;
    }

}
