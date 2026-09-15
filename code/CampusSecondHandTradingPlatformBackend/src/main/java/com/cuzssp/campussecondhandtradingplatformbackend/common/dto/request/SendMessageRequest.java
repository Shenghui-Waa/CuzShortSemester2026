package com.cuzssp.campussecondhandtradingplatform_backend.common.dto;

import lombok.Data;

@Data
public class SendMessageRequest {
    private Long receiverId;
    private Long productId;
    private String content;
}
