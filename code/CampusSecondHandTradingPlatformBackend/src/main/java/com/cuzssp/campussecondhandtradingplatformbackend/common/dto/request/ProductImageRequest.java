package com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductImageRequest {

    @NotBlank
    @Size(max = 255)
    @Pattern(regexp = "^https?://.+")
    private String url;

    @Min(1)
    private Integer sortOrder;

}
