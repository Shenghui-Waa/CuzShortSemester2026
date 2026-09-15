package com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {

    private String nickname;

    private String phone;

    private String email;

    private String school;

    private String campus;

}
