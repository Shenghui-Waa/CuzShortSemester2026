package com.cuzssp.campussecondhandtradingplatformbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Favorite;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {

    @Select("""
            SELECT f.* FROM favorite f
            JOIN product p ON p.id = f.product_id
            WHERE f.user_id = #{userId}
                AND p.status = 1 AND p.is_deleted = 0
            ORDER BY f.created_at DESC, f.id DESC
            """)
    List<Favorite> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM favorite WHERE user_id = #{userId} AND product_id = #{productId}")
    long countByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Delete("DELETE FROM favorite WHERE user_id = #{userId} AND product_id = #{productId}")
    int deleteByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Select("""
            SELECT f.product_id FROM favorite f
            JOIN product p ON p.id = f.product_id
            WHERE f.user_id = #{userId} AND p.is_deleted = 0
            """)
    List<Long> selectFavoriteProductIdsByUserId(@Param("userId") Long userId);

    @Delete("DELETE FROM favorite WHERE product_id = #{productId}")
    int deleteByProductId(@Param("productId") Long productId);

}
