package com.cuzssp.campussecondhandtradingplatform_backend.common.dto;

import lombok.Data;

@Data
public class CreateOrderRequest {
    private Long productId;
    private String remark;
}
