package com.cuzssp.campussecondhandtradingplatform_backend.common.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemVO {
    private Long id;
    private Long productId;
    private String productTitle;
    private String productImage;
    private BigDecimal price;
}
