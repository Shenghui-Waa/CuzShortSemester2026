package com.cuzssp.campussecondhandtradingplatformbackend.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_info")
public class OrderInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo;

    @TableField("buyer_id")
    private Long buyerId;

    @TableField("seller_id")
    private Long sellerId;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("status")
    // 0=待付款 1=待发货 2=待收货 3=已完成 4=已取消
    private Integer status;

    @TableField("remark")
    private String remark;

    @TableField(value = "created_at",fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill =  FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField("paid_at")
    private LocalDateTime paidAt;

    @TableField("shipped_at")
    private LocalDateTime shippedAt;

    @TableField("completed_at")
    private LocalDateTime completedAt;

    @TableField("refund_status")
    // 0=未退款/无需退款 1=已退款
    private Integer refundStatus;

    @TableField("refunded_at")
    private LocalDateTime refundedAt;

}
