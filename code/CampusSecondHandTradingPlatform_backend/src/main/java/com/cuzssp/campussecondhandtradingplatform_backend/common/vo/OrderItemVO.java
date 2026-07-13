package com.cuzssp.campussecondhandtradingplatform_backend.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemVO {
    private Long id;
    private Long productId;
    private String productTitle;
    private String productImage;
    private BigDecimal price;
}
