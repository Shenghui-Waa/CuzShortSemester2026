package com.cuzssp.campussecondhandtradingplatform_backend.mapper;

import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.OrderItem;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface OrderItemMapper {
    @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
    List<OrderItem> selectByOrderId(@Param("orderId") Long orderId);

    @Insert("INSERT INTO order_item (order_id, product_id, price, created_at) VALUES (#{orderId}, #{productId}, #{price}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrderItem item);
}
