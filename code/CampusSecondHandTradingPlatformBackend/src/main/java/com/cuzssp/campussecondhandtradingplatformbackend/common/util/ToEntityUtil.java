package com.cuzssp.campussecondhandtradingplatformbackend.common.util;



import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.ChatMessageConstant;
import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.OrderInfoConstant;
import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.UserConstant;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.AnnouncementRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.CartItemRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.CategoryRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.ChatMessageRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.FavoriteRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.OrderInfoRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.OrderItemRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.ProductImageRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.ProductRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.ReviewRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.UserRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.*;
import com.cuzssp.campussecondhandtradingplatformbackend.common.security.PasswordProvider;

import java.time.LocalDateTime;
import java.util.UUID;

public class ToEntityUtil {

    // 用户实体
    public static User toUserEntity(
            UserRequest request, PasswordProvider passwordProvider, int role
    ){
        User user = new User();

        user.setUsername(request.getUsername());
        user.setPassword(passwordProvider.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null
                ? request.getNickname() : request.getUsername()
        );
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setSchool(request.getSchool());
        user.setCampus(request.getCampus());
        user.setAvatar(request.getAvatar());
        user.setRole(role);
        user.setStatus(UserConstant.Status.ACTIVE);
        user.setCreditScore(UserConstant.CREDIT_SCORE_DEFAULT);
        user.setCreatedAt(UtcTime.now());
        user.setUpdatedAt(UtcTime.now());

        return user;
    }

    // 更新用户实体
    public static User updateUserEntity(
            User user, UserRequest request
    ) {
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setSchool(request.getSchool());
        user.setCampus(request.getCampus());
        user.setUpdatedAt(UtcTime.now());
        return user;
    }

    // 分类实体
    public static Category toCategoryEntity(
            CategoryRequest request
    ) {
        Category category = new Category();
        category.setName(request.getName());
        category.setIcon(request.getIcon());
        category.setSortOrder(request.getSortOrder());
        category.setCreatedAt(UtcTime.now());
        return category;
    }

    // 商品实体
    public static Product toProductEntity(
            ProductRequest request
    ) {
        Product product = new Product();
        product.setCategoryId(request.getCategoryId());
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setState(request.getState());
        product.setCampus(request.getCampus());
        return product;
    }

    // 商品图片实体
    public static ProductImage toProductImageEntity(
            ProductImageRequest request, Long productId, int defaultSortOrder
    ) {
        ProductImage productImage = new ProductImage();
        productImage.setProductId(productId);
        productImage.setUrl(request.getUrl());
        productImage.setSortOrder(request.getSortOrder() != null
                ? request.getSortOrder()
                : defaultSortOrder);
        return productImage;
    }

    // 购物车物品实体
    public static CartItem toCartItemEntity(
            Long userId, CartItemRequest request
    ) {
        CartItem cartItem = new CartItem();
        cartItem.setUserId(userId);
        cartItem.setProductId(request.getProductId());
        cartItem.setCreatedAt(UtcTime.now());
        return cartItem;
    }

    // 收藏物品实体
    public static Favorite toFavoriteEntity(
            Long userId, FavoriteRequest request
    ) {
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(request.getProductId());
        favorite.setCreatedAt(UtcTime.now());
        return favorite;
    }

    // 订单实体
    public static OrderInfo toOrderInfoEntity(
            Long buyerId, Product product, OrderInfoRequest request
    ) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderNo(UUID.randomUUID().toString().replace("-","").substring(0,20));
        orderInfo.setBuyerId(buyerId);
        orderInfo.setSellerId(product.getUserId());
        orderInfo.setTotalAmount(product.getPrice());
        orderInfo.setStatus(OrderInfoConstant.Status.WAIT_PAY);
        orderInfo.setRefundStatus(OrderInfoConstant.RefundStatus.NONE);
        orderInfo.setRefundedAt(null);
        orderInfo.setRemark(request.getRemark());
        orderInfo.setCreatedAt(UtcTime.now());
        return orderInfo;
    }

    // 订单物品实体
    public static OrderItem toOrderItemEntity(
            OrderInfo orderInfo, OrderItemRequest request
    ) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(orderInfo.getId());
        orderItem.setProductId(request.getProductId());
        orderItem.setPrice(request.getPrice());
        orderItem.setProductTitle(request.getProductTitle());
        orderItem.setProductImage(request.getProductImage());
        orderItem.setProductState(request.getProductState());
        orderItem.setCreatedAt(UtcTime.now());
        return orderItem;
    }

    // 评价实体
    public static Review toReviewEntity(
            Long reviewerId, ReviewRequest request
    ) {
        Review review = new Review();
        review.setOrderId(request.getOrderId());
        review.setReviewerId(reviewerId);
        review.setTargetId(request.getTargetId());
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setCreatedAt(UtcTime.now());
        return review;
    }

    // 聊天信息实体
    public static ChatMessage toChatMessageEntity(
            Long senderId, ChatMessageRequest request
    ) {
        ChatMessage msg = new ChatMessage();
        msg.setSenderId(senderId);
        msg.setReceiverId(request.getReceiverId());
        msg.setProductId(request.getProductId());
        msg.setContent(request.getContent());
        msg.setIsRead(ChatMessageConstant.ReadStatus.NO);
        msg.setCreatedAt(UtcTime.now());
        return msg;
    }

    // 公告实体
    public static Announcement toAnnouncementEntity(
            AnnouncementRequest announcementRequest
    ) {
        Announcement announcement = new Announcement();
        announcement.setTitle(announcementRequest.getTitle());
        announcement.setContent(announcementRequest.getContent());
        announcement.setCreatedAt(UtcTime.now());
        announcement.setUpdatedAt(UtcTime.now());
        return announcement;
    }

}
