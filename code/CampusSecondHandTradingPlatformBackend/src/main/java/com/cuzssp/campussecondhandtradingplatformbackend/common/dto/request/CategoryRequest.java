package com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank
    @Size(max = 32)
    private String name;

    @Size(max = 255)
    private String icon;

    @Min(0)
    private Integer sortOrder;

}
