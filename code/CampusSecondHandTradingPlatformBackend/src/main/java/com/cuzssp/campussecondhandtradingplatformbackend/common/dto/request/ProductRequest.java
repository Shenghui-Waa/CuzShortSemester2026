package com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequest {

    @NotBlank
    @Size(max = 128)
    private String title;

    private String description;

    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal price;

    @DecimalMin("0.00")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal originalPrice;

    @NotNull
    @Min(1)
    @Max(3)
    private Integer state;

    @NotBlank
    @Size(max = 32)
    private String campus;

    @NotNull
    private Long categoryId;

    @Valid
    @NotNull
    @Size(max = 6)
    private List<ProductImageRequest> images;

}
