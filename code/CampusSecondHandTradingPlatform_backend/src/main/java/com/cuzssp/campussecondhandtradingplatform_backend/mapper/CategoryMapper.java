package com.cuzssp.campussecondhandtradingplatform_backend.mapper;

import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.Category;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CategoryMapper {
    @Select("SELECT * FROM category WHERE id = #{id}")
    Category selectById(@Param("id") Long id);

    @Select("SELECT * FROM category ORDER BY sort_order ASC")
    List<Category> selectAll();

    @Insert("INSERT INTO category (name, icon, sort_order, created_at) VALUES (#{name}, #{icon}, #{sortOrder}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Category category);

    @Update("UPDATE category SET name=#{name}, icon=#{icon}, sort_order=#{sortOrder} WHERE id=#{id}")
    int updateById(Category category);

    @Delete("DELETE FROM category WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM product WHERE category_id = #{categoryId}")
    int countProductsByCategoryId(@Param("categoryId") Long categoryId);
}
