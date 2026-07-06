package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.CartItem;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.Product;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.ProductImage;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.CartMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.ProductMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.ProductImageMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.service.CartService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.CartItemVO;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    @Autowired private CartMapper cartMapper;
    @Autowired private ProductMapper productMapper;
    @Autowired private ProductImageMapper productImageMapper;

    @Override
    public Result<List<CartItemVO>> getCart(Long userId) {
        List<CartItem> items = cartMapper.selectByUserId(userId);
        List<CartItemVO> vos = items.stream().map(item -> {
            CartItemVO vo = new CartItemVO();
            vo.setId(item.getId());
            Product p = productMapper.selectById(item.getProductId());
            if (p != null) {
                vo.setProductId(p.getId()); vo.setProductTitle(p.getTitle()); vo.setPrice(p.getPrice());
                List<ProductImage> imgs = productImageMapper.selectByProductId(p.getId());
                vo.setProductImage(imgs.isEmpty() ? null : imgs.get(0).getUrl());
            }
            return vo;
        }).collect(Collectors.toList());
        return Result.success(vos);
    }

    @Override
    public Result<Void> addToCart(Long userId, Long productId) {
        if (cartMapper.countByUserIdAndProductId(userId, productId) > 0) return Result.success();
        CartItem item = new CartItem();
        item.setUserId(userId);
        item.setProductId(productId);
        item.setCreatedAt(LocalDateTime.now());
        cartMapper.insert(item);
        return Result.success();
    }

    @Override
    public Result<Void> removeFromCart(Long userId, Long cartId) {
        cartMapper.deleteById(cartId);
        return Result.success();
    }
}
