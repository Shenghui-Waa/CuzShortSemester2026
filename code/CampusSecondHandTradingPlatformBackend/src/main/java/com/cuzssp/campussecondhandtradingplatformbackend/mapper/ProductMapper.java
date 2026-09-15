package com.cuzssp.campussecondhandtradingplatformbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    List<Product> selectByKeywordOrCategoryOrCampusOrStatus(
            @Param("keyword") String keyword, @Param("categoryId") Long categoryId,
            @Param("campus") String campus, @Param("status") Integer status);

    @Select("SELECT * FROM product WHERE user_id = #{userId} ORDER BY created_at DESC, id DESC")
    List<Product> selectByUserIdWithLimit(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM product WHERE category_id = #{categoryId}")
    Long countByCategoryId(@Param("categoryId") Long categoryId);

    @Update("UPDATE product SET view_count = COALESCE(view_count, 0) + 1 WHERE id = #{id}")
    int addViewCount(@Param("id") Long id);

    @Update("""
            UPDATE product SET category_id = #{product.categoryId}, title = #{product.title},
                description = #{product.description}, price = #{product.price},
                original_price = #{product.originalPrice}, state = #{product.state},
                campus = #{product.campus}, status = #{product.status}, updated_at = #{product.updatedAt}
            WHERE id = #{product.id} AND user_id = #{product.userId} AND status = #{expectedStatus}
            """)
    int updateDetailsIfStatusMatches(@Param("product") Product product,
                                    @Param("expectedStatus") Integer expectedStatus);

    @Update("""
            UPDATE product SET updated_at = #{updatedAt}
            WHERE id = #{id} AND user_id = #{userId} AND status = #{expectedStatus}
            """)
    int lockForRemoval(@Param("id") Long id, @Param("userId") Long userId,
                       @Param("expectedStatus") Integer expectedStatus,
                       @Param("updatedAt") LocalDateTime updatedAt);

    @Update("""
            UPDATE product SET status = #{targetStatus}, updated_at = #{updatedAt}
            WHERE id = #{id} AND status = #{expectedStatus}
            """)
    int updateStatusIfMatches(@Param("id") Long id, @Param("expectedStatus") Integer expectedStatus,
                              @Param("targetStatus") Integer targetStatus,
                              @Param("updatedAt") LocalDateTime updatedAt);

    @Update("""
            UPDATE product SET status = #{targetStatus}, updated_at = #{updatedAt}
            WHERE id = #{id} AND status = #{expectedStatus} AND price = #{expectedPrice}
            """)
    int reserveIfAvailable(@Param("id") Long id, @Param("expectedStatus") Integer expectedStatus,
                           @Param("expectedPrice") BigDecimal expectedPrice,
                           @Param("targetStatus") Integer targetStatus,
                           @Param("updatedAt") LocalDateTime updatedAt);

    @Update("""
            UPDATE product SET status = #{targetStatus}, updated_at = #{updatedAt}
            WHERE user_id = #{userId} AND status != 2
            """)
    int updateUserProductsStatus(@Param("userId") Long userId,
                                 @Param("targetStatus") Integer targetStatus,
                                 @Param("updatedAt") LocalDateTime updatedAt);
}
