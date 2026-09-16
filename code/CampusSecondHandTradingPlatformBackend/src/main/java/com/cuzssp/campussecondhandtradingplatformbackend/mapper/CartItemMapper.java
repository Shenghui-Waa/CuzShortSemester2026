package com.cuzssp.campussecondhandtradingplatformbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.CartItem;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {

    @Select("""
            SELECT c.* FROM cart_item c
            JOIN product p ON p.id = c.product_id
            WHERE c.user_id = #{userId}
                AND p.status = 1 AND p.is_deleted = 0
            ORDER BY c.created_at DESC, c.id DESC
            """)
    List<CartItem> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM cart_item WHERE user_id = #{userId} AND product_id = #{productId}")
    long countByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Delete("DELETE FROM cart_item WHERE user_id = #{userId} AND product_id = #{productId}")
    int deleteByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Delete("DELETE FROM cart_item WHERE product_id = #{productId}")
    int deleteByProductId(@Param("productId") Long productId);

}
