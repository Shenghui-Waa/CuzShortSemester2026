package com.cuzssp.campussecondhandtradingplatform_backend.common.util;

import com.cuzssp.campussecondhandtradingplatform_backend.common.constant.ChatMessageConstant;
import com.cuzssp.campussecondhandtradingplatform_backend.common.constant.OrderInfoConstant;
import com.cuzssp.campussecondhandtradingplatform_backend.common.constant.UserConstant;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.RegisterRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.ReviewRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.*;
import com.cuzssp.campussecondhandtradingplatform_backend.common.security.Base64Provider;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ToEntityUtil {

    public static User toUserEntity(
            RegisterRequest request, Base64Provider base64Provider
    ){
        User user = new User();

        user.setUsername(request.getUsername());
        user.setPassword(base64Provider.encode(request.getPassword()));
        user.setNickname(
                request.getNickname() != null ?
                request.getNickname()
                : request.getUsername()
        );
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setSchool(request.getSchool());
        user.setCampus(request.getCampus());
        user.setRole(UserConstant.ROLE_USER);
        user.setStatus(UserConstant.STATUS_ABLE);
        user.setCreditScore(UserConstant.CREDIT_SCORE_DEFAULT);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return user;
    }

    public static CartItem toCartItemEntity(
            Long userId, Long productId
    ) {
        CartItem cartItem = new CartItem();
        cartItem.setUserId(userId);
        cartItem.setProductId(productId);
        cartItem.setCreatedAt(LocalDateTime.now());
        return cartItem;
    }

    public static Favorite toFavoriteEntity(
            Long userId, Long productId
    ) {
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favorite.setCreatedAt(LocalDateTime.now());
        return favorite;
    }

    public static OrderInfo toOrderInfoEntity(
            Long buyerId, Product product, String remark
    ) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderNo(UUID.randomUUID().toString().replace("-","").substring(0,20));
        orderInfo.setBuyerId(buyerId);
        orderInfo.setSellerId(product.getUserId());
        orderInfo.setTotalAmount(product.getPrice());
        orderInfo.setStatus(OrderInfoConstant.STATUS_WAIT_PAY);
        orderInfo.setRemark(remark);
        orderInfo.setCreatedAt(LocalDateTime.now());
        return orderInfo;
    }

    public static OrderItem toOrderItemEntity(
            OrderInfo orderInfo, Product product
    ) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(orderInfo.getId());
        orderItem.setProductId(product.getId());
        orderItem.setPrice(product.getPrice());
        orderItem.setCreatedAt(LocalDateTime.now());
        return orderItem;
    }

    public static Review toReviewEntity(
            Long reviewerId, ReviewRequest request
    ) {
        Review review = new Review();
        review.setOrderId(request.getOrderId());
        review.setReviewerId(reviewerId);
        review.setTargetId(request.getTargetId());
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setCreatedAt(LocalDateTime.now());
        return review;
    }

    public static ChatMessage toChatMessageEntity(
            Long senderId, Long receiverId, Long productId, String content
    ) {
        ChatMessage msg = new ChatMessage();
        msg.setSenderId(senderId);
        msg.setReceiverId(receiverId);
        msg.setProductId(productId);
        msg.setContent(content);
        msg.setIsRead(ChatMessageConstant.READ_STATUS_NO);
        msg.setCreatedAt(LocalDateTime.now());

        return msg;
    }
}
