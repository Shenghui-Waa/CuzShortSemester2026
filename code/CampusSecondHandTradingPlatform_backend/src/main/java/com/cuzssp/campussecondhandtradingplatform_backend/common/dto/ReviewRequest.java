package com.cuzssp.campussecondhandtradingplatform_backend.common.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private Long orderId;
    private Long targetId;
    private Integer rating;
    private String content;
}
