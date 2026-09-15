package com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request;

import lombok.Data;

@Data
public class ProductQueryRequest {

    private String keyword;

    private Long categoryId;

    private String campus;

    private Integer page = 1;

    private Integer pageSize = 12;

}
