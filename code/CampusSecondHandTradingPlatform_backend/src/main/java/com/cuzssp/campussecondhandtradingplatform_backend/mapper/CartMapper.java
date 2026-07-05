package com.cuzssp.campussecondhandtradingplatform_backend.mapper;

import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.CartItem;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CartMapper {
    @Select("SELECT * FROM cart_item WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<CartItem> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM cart_item WHERE id = #{id}")
    CartItem selectById(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM cart_item WHERE user_id = #{userId} AND product_id = #{productId}")
    int countByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Insert("INSERT INTO cart_item (user_id, product_id, created_at) VALUES (#{userId}, #{productId}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CartItem item);

    @Delete("DELETE FROM cart_item WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
