package com.cuzssp.campussecondhandtradingplatform_backend.common.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatMessageVO {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private Long productId;
    private String content;
    private Integer isRead;
    private LocalDateTime createdAt;
}
