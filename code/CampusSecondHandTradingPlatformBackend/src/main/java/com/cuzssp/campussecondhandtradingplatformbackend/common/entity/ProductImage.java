package com.cuzssp.campussecondhandtradingplatformbackend.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("product_image")
public class ProductImage {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("product_id")
    private Long productId;

    @TableField("url")
    private String url;

    @TableField("sort_order")
    // 排序（首图=1）
    private Integer sortOrder;

}
