package com.cuzssp.campussecondhandtradingplatformbackend.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("nickname")
    private String nickname;

    @TableField("avatar")
    private String avatar;

    @TableField("phone")
    private String phone;

    @TableField("email")
    private String email;

    @TableField("school")
    private String school;

    @TableField("campus")
    private String campus;

    @TableField("role")
    // 角色：0=用户 1=管理员
    private Integer role;

    @TableField("status")
    // 状态：0=正常 1=封禁
    private Integer status;

    @TableField("credit_score")
    // 信誉分，默认100
    private Integer creditScore;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

}
