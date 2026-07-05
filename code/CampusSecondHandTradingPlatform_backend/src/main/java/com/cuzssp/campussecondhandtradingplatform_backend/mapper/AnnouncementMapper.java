package com.cuzssp.campussecondhandtradingplatform_backend.mapper;

import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.Announcement;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AnnouncementMapper {
    @Select("SELECT * FROM announcement ORDER BY created_at DESC")
    List<Announcement> selectAll();

    @Select("SELECT * FROM announcement WHERE id = #{id}")
    Announcement selectById(@Param("id") Long id);

    @Insert("INSERT INTO announcement (title, content, created_at, updated_at) VALUES (#{title}, #{content}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Announcement announcement);

    @Update("UPDATE announcement SET title=#{title}, content=#{content}, updated_at=#{updatedAt} WHERE id=#{id}")
    int updateById(Announcement announcement);

    @Delete("DELETE FROM announcement WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
