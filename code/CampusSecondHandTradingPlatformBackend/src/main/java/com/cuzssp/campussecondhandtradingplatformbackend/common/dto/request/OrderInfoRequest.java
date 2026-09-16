package com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrderInfoRequest {

    @NotNull
    private Long productId;

    @Size(max = 255)
    private String remark;

}
