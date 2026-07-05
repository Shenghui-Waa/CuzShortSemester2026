package com.cuzssp.campussecondhandtradingplatform_backend.common.vo;

import lombok.Data;

@Data
public class CategoryVO {
    private Long id;
    private String name;
    private String icon;
    private Integer sortOrder;
    private Long productCount;
}
