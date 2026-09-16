package com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemRequest {

    @NotNull
    private Long productId;

    @NotNull
    private BigDecimal price;

    private String productTitle;

    private String productImage;

    private Integer productState;

}
