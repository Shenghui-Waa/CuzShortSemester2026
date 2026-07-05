package com.cuzssp.campussecondhandtradingplatform_backend.common.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CartItemVO {
    private Long id;
    private Long productId;
    private String productTitle;
    private String productImage;
    private BigDecimal price;
    private String sellerName;
    private LocalDateTime createdAt;
}
