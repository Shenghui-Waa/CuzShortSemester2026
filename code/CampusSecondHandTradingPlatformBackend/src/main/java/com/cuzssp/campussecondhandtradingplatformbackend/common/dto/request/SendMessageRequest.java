package com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request;

import lombok.Data;

@Data
public class SendMessageRequest {

    private Long receiverId;

    private Long productId;

    private String content;

}
