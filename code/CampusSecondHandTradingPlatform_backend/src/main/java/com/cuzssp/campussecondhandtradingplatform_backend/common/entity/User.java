package com.cuzssp.campussecondhandtradingplatform_backend.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("`user`")
public class User {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String avatar;

    private String phone;

    private String email;

    private String school;

    private String campus;

    /** 角色：0=用户 1=管理员 */
    private Integer role;

    /** 状态：0=正常 1=封禁 */
    private Integer status;

    /** 信誉分，默认100 */
    private Integer creditScore;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

}
