package com.cuzssp.campussecondhandtradingplatform_backend.common.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String phone;
    private String email;
    private String school;
    private String campus;
    private Integer role;
    private Integer status;
    private Integer creditScore;
    private LocalDateTime createdAt;
}
