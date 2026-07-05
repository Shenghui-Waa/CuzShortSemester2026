package com.cuzssp.campussecondhandtradingplatform_backend.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("product_image")
public class ProductImage {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long productId;

    private String url;

    /** 排序（首图=1） */
    private Integer sortOrder;

}
