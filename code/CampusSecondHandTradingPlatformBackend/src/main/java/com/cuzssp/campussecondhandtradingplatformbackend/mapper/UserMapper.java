package com.cuzssp.campussecondhandtradingplatformbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.UpdateProfileRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectByUsername(@Param("username") String username);

    @Select("SELECT COUNT(*) FROM user WHERE username = #{username}")
    Long countByUsername(@Param("username") String username);

    @Select("SELECT * FROM user ORDER BY id ASC")
    List<User> selectAll();

    @Select("""
            SELECT * FROM user
            WHERE username LIKE #{pattern} OR nickname LIKE #{pattern} OR school LIKE #{pattern}
            ORDER BY created_at ASC, id ASC
            """)
    List<User> selectByKeyword(@Param("pattern") String pattern);

    @Select("""
            SELECT COUNT(*) FROM user
            WHERE created_at >= #{start} AND created_at < #{end}
            """)
    Long countCreatedBetween(@Param("start") LocalDateTime start,
                            @Param("end") LocalDateTime end);

    int updateProfileFields(@Param("userId") Long userId,
                            @Param("request") UpdateProfileRequest request,
                            @Param("updatedAt") LocalDateTime updatedAt);

    @Update("""
            UPDATE user SET password = #{password}, updated_at = #{updatedAt}
            WHERE id = #{userId}
            """)
    int updatePassword(@Param("userId") Long userId, @Param("password") String password,
                       @Param("updatedAt") LocalDateTime updatedAt);

    @Update("""
            UPDATE user SET avatar = #{avatar}, updated_at = #{updatedAt}
            WHERE id = #{userId}
            """)
    int updateAvatar(@Param("userId") Long userId, @Param("avatar") String avatar,
                     @Param("updatedAt") LocalDateTime updatedAt);

    @Update("""
            UPDATE user SET status = #{status}, updated_at = #{updatedAt}
            WHERE id = #{userId}
            """)
    int updateStatus(@Param("userId") Long userId, @Param("status") Integer status,
                     @Param("updatedAt") LocalDateTime updatedAt);

    @Update("""
            UPDATE user SET credit_score = COALESCE(credit_score, 0) + #{adjustment},
                updated_at = #{updatedAt}
            WHERE id = #{userId}
            """)
    int adjustCreditScore(@Param("userId") Long userId, @Param("adjustment") int adjustment,
                          @Param("updatedAt") LocalDateTime updatedAt);
}
