package com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {

    public interface Create {
    }

    public interface Update {
    }

    @NotBlank(groups = Create.class)
    @Size(max = 32)
    private String username;

    @NotBlank(groups = Create.class)
    @Size(max = 128)
    private String password;

    @Size(max = 32)
    private String nickname;

    @Size(max = 255)
    private String avatar;

    @Size(max = 16)
    private String phone;

    @Size(max = 64)
    private String email;

    @Size(max = 64)
    private String school;

    @Size(max = 32)
    private String campus;

}
