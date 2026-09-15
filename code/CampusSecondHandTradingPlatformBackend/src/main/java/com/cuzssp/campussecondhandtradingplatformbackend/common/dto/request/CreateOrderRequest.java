package com.cuzssp.campussecondhandtradingplatform_backend.common.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderRequest {
    @NotNull(message = "Product ID is required")
    private Long productId;
    private String remark;
}
