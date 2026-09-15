package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;

import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.CartItem;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.Product;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToEntityUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToVOUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.CartMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.ProductMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.ProductImageMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.UserMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.service.CartService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.Result;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.CartItemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;
    private final ProductMapper productMapper;
    private final ProductImageMapper productImageMapper;
    private final UserMapper userMapper;

    // 获取购物车
    @Override
    public Result<List<CartItemVO>> getCart(Long userId) {
        List<CartItem> cartItems = cartMapper.selectByUserId(userId);
        List<CartItemVO> cartItemVOs = cartItems.stream()
                .map(cartItem -> {
                    Product product = productMapper.selectById(cartItem.getProductId());
                    User seller = product != null
                            ? userMapper.selectById(product.getUserId())
                            : null;
                    return ToVOUtil.toCartItemVO(
                            cartItem, product,
                            seller, productImageMapper
                    );
                }).collect(Collectors.toList());
        return Result.success(cartItemVOs);
    }

    // 加入购物车
    @Override
    public Result<Void> addToCart(Long userId, Long productId) {
        if (cartMapper.countByUserIdAndProductId(userId, productId) > 0)
            return Result.success();
        CartItem cartItem = ToEntityUtil.toCartItemEntity(userId, productId);
        cartMapper.insertItem(cartItem);
        return Result.success();
    }

    // 移出购物车
    @Override
    public Result<Void> removeFromCart(Long userId, Long productId) {
        cartMapper.deleteById(userId, productId);
        return Result.success();
    }
}
