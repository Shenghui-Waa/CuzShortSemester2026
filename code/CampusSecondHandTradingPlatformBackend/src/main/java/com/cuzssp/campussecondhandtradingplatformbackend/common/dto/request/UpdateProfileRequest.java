package com.cuzssp.campussecondhandtradingplatform_backend.common.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String nickname;
    private String phone;
    private String email;
    private String school;
    private String campus;
}
