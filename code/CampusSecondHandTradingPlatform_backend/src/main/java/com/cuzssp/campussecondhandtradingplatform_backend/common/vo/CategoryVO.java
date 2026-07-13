package com.cuzssp.campussecondhandtradingplatform_backend.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryVO {
    private Long id;
    private String name;
    private String icon;
    private Integer sortOrder;
    private Long productCount;
}
