package com.cuzssp.campussecondhandtradingplatformbackend.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;

@Mapper
public interface RevokedTokenMapper {

    @Select("SELECT COUNT(1) FROM revoked_token WHERE jti = #{jti}")
    int countByJti(@Param("jti") String jti);

    @Insert("""
            INSERT INTO revoked_token (jti, expires_at)
            VALUES (#{jti}, #{expiresAt})
            """)
    int insertRevocation(
            @Param("jti") String jti,
            @Param("expiresAt") Date expiresAt
    );

    @Delete("DELETE FROM revoked_token WHERE expires_at <= #{now}")
    int deleteExpired(@Param("now") Date now);
}
