package com.cuzssp.campussecondhandtradingplatform_backend.service;

import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.CartItemVO;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.Result;
import java.util.List;

public interface CartService {

    Result<List<CartItemVO>> getCart(Long userId);
    Result<Void> addToCart(Long userId, Long productId);
    Result<Void> removeFromCart(Long userId, Long productId);

}
