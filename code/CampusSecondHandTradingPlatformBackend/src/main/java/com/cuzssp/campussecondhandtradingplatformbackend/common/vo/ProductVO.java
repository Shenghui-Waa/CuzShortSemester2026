package com.cuzssp.campussecondhandtradingplatformbackend.common.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductVO {
    private Long id;
    private Long userId;
    private String sellerName;
    private String sellerAvatar;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer state;
    private String campus;
    private Integer status;
    private Integer viewCount;
    private List<String> images;
    private Boolean isFavorite;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
