package com.cuzssp.campussecondhandtradingplatform_backend.common.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewVO {
    private Long id;
    private Long orderId;
    private Long reviewerId;
    private String reviewerName;
    private String reviewerAvatar;
    private Long targetId;
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;
}
