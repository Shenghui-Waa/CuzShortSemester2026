package com.cuzssp.campussecondhandtradingplatform_backend.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_item")
public class OrderItem {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long orderId;

    private Long productId;

    private BigDecimal price;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

}
