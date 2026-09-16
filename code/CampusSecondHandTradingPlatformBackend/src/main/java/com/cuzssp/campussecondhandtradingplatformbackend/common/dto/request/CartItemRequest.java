package com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemRequest {

    @NotNull
    private Long productId;

}
