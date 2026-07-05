package com.cuzssp.campussecondhandtradingplatform_backend.mapper;

import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM `user` WHERE id = #{id}")
    User selectById(@Param("id") Long id);

    @Select("SELECT * FROM `user` WHERE username = #{username}")
    User selectByUsername(@Param("username") String username);

    @Select("SELECT COUNT(*) FROM `user` WHERE username = #{username}")
    int countByUsername(@Param("username") String username);

    @Select("SELECT COUNT(*) FROM `user`")
    int countAll();

    @Select("SELECT COUNT(*) FROM `user` WHERE DATE(created_at) = CURDATE()")
    int countTodayNew();

    @Select("SELECT * FROM `user` WHERE status = #{status} ORDER BY created_at DESC")
    List<User> selectByStatus(@Param("status") Integer status);

    @Select("SELECT * FROM `user`")
    List<User> selectAll();

    @Insert("INSERT INTO `user` (username, password, nickname, avatar, phone, email, school, campus, role, status, credit_score, created_at, updated_at) VALUES (#{username}, #{password}, #{nickname}, #{avatar}, #{phone}, #{email}, #{school}, #{campus}, #{role}, #{status}, #{creditScore}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE `user` SET username=#{username}, password=#{password}, nickname=#{nickname}, avatar=#{avatar}, phone=#{phone}, email=#{email}, school=#{school}, campus=#{campus}, role=#{role}, status=#{status}, credit_score=#{creditScore}, updated_at=#{updatedAt} WHERE id=#{id}")
    int updateById(User user);
}
