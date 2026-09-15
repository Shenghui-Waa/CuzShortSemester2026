package com.cuzssp.campussecondhandtradingplatform_backend.common.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Password is required")
    private String password;
    private String nickname;
    private String phone;
    private String email;
    private String school;
    private String campus;
}
