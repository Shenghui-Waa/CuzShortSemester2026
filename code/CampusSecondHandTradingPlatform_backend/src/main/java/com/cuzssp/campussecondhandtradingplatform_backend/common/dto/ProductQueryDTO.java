package com.cuzssp.campussecondhandtradingplatform_backend.common.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductQueryDTO {
    private String keyword;
    private Long categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer condition;
    private String campus;
    private Integer page = 1;
    private Integer pageSize = 12;
    private String sortBy = "created_at";
    private String sortOrder = "desc";
}
