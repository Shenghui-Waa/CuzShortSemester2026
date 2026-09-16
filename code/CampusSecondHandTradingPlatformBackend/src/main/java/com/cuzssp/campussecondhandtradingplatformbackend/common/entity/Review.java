package com.cuzssp.campussecondhandtradingplatformbackend.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("review")
public class Review {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_id")
    private Long orderId;

    @TableField("reviewer_id")
    private Long reviewerId;

    @TableField("target_id")
    private Long targetId;

    @TableField("rating")
    // 评分 1-5
    private Integer rating;

    @TableField("content")
    private String content;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

}
