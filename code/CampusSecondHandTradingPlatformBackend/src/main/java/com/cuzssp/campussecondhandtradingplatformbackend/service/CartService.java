package com.cuzssp.campussecondhandtradingplatformbackend.service;

import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.CartItemRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.CartItemVO;
import java.util.List;

public interface CartService {

    List<CartItemVO> getCart(Long userId);
    Void addToCart(Long userId, CartItemRequest request);
    Void removeFromCart(Long userId, Long productId);

}
