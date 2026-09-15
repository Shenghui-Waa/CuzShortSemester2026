package com.cuzssp.campussecondhandtradingplatform_backend.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("category")
public class Category {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String name;

    private String icon;

    private Integer sortOrder;

    private LocalDateTime createdAt;

}
