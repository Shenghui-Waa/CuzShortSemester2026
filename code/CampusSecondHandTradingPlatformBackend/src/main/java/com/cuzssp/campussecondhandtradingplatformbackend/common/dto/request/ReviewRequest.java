package com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @Size(max = 500)
    private String content;

}
