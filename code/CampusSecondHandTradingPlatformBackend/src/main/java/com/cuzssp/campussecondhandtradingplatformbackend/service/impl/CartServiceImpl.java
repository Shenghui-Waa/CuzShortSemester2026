package com.cuzssp.campussecondhandtradingplatformbackend.service.impl;

import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.ProductConstant;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.Result;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.CartItemRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.CartItem;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Product;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.ToEntityUtil;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.ToVOUtil;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.CartItemMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.ProductMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.ProductImageMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.UserMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.service.CartService;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.CartItemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.cuzssp.campussecondhandtradingplatformbackend.common.exception.BusinessException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemMapper cartMapper;
    private final ProductMapper productMapper;
    private final ProductImageMapper productImageMapper;
    private final UserMapper userMapper;

    // 获取购物车
    @Override
    public List<CartItemVO> getCart(Long userId) {
        List<CartItem> cartItems = cartMapper.selectByUserId(userId);
        return cartItems
                .stream()
                .map(cartItem -> {
                    Product product = productMapper.selectById(cartItem.getProductId());
                    User seller = product != null
                            ? userMapper.selectById(product.getUserId())
                            : null;
                    return ToVOUtil.toCartItemVO(
                            cartItem, product,
                            seller, productImageMapper.selectByProductId(cartItem.getProductId())
                    );
                })
                .collect(Collectors.toList());
    }

    // 加入购物车
    @Override
    public Void addToCart(Long userId, CartItemRequest request) {
        Product product = productMapper.selectById(request.getProductId());
        if (product == null)
            throw new BusinessException(Result.Code.NOT_FOUND, "Product not found");

        if (!Objects.equals(product.getStatus(), ProductConstant.Status.ON_SALE))
            throw new BusinessException("Product is not available");

        if (Objects.equals(product.getUserId(), userId))
            throw new BusinessException("Seller cannot add own product to cart");

        if (cartMapper.countByUserIdAndProductId(userId, request.getProductId()) > 0)
            return null;

        CartItem cartItem = ToEntityUtil.toCartItemEntity(userId, request);
        cartMapper.insert(cartItem);
        return null;
    }

    // 移出购物车
    @Override
    public Void removeFromCart(Long userId, Long productId) {
        cartMapper.deleteByUserIdAndProductId(userId, productId);
        return null;
    }
}
