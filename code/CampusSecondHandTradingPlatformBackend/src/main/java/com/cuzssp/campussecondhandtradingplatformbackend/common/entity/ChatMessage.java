package com.cuzssp.campussecondhandtradingplatform_backend.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("chat_message")
public class ChatMessage {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long senderId;

    private Long receiverId;

    private Long productId;

    private String content;

    /** 0=未读 1=已读 */
    private Integer isRead;

    private LocalDateTime createdAt;

}
