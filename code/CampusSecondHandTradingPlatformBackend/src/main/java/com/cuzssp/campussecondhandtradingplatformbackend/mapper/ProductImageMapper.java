package com.cuzssp.campussecondhandtradingplatformbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.ProductImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;
import java.util.List;

@Mapper
public interface ProductImageMapper extends BaseMapper<ProductImage> {
    @Select("SELECT * FROM product_image WHERE product_id = #{productId} ORDER BY sort_order, id")
    List<ProductImage> selectByProductId(@Param("productId") Long productId);

    List<ProductImage> selectByProductIds(@Param("productIds") List<Long> productIds);

    @Delete("DELETE FROM product_image WHERE product_id = #{productId}")
    int deleteByProductId(@Param("productId") Long productId);
}
