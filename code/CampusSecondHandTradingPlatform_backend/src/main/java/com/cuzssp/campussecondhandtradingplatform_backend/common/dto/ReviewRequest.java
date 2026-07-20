package com.cuzssp.campussecondhandtradingplatform_backend.common.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequest {
    @NotNull(message = "Order ID is required")
    private Long orderId;
    @NotNull(message = "Target ID is required")
    private Long targetId;
    @NotNull(message = "Rating is required")
    @Min(1) @Max(5)
    private Integer rating;
    private String content;
}
