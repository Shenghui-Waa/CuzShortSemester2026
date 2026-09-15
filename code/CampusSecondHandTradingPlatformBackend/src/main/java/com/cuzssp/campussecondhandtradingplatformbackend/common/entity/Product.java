package com.cuzssp.campussecondhandtradingplatformbackend.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("category_id")
    private Long categoryId;

    @TableField("title")
    private String title;

    @TableField("description")
    private String description;

    @TableField("price")
    private BigDecimal price;

    @TableField("original_price")
    private BigDecimal originalPrice;

    @TableField("state")
    // 新旧：1=全新 2=几乎全新 3=有使用痕迹
    private Integer state;

    @TableField("campus")
    private String campus;

    @TableField("status")
    // 状态：0=待审核 1=在售 2=已售出 3=已下架
    private Integer status;

    @TableField("view_count")
    // 浏览量，默认0
    private Integer viewCount;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

}
