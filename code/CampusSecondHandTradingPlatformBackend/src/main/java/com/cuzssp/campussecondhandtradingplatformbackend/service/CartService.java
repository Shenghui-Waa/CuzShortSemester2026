package com.cuzssp.campussecondhandtradingplatformbackend.service;

import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.CartItemVO;
import java.util.List;

public interface CartService {

    List<CartItemVO> getCart(Long userId);
    Void addToCart(Long userId, Long productId);
    Void removeFromCart(Long userId, Long productId);

}
