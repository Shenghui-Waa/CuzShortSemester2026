package com.cuzssp.campussecondhandtradingplatform_backend.common.dto;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
}
