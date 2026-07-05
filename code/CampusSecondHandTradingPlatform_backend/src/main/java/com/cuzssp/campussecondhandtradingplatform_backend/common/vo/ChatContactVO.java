package com.cuzssp.campussecondhandtradingplatform_backend.common.vo;

import lombok.Data;

@Data
public class ChatContactVO {
    private Long contactId;
    private String contactName;
    private String contactAvatar;
    private String lastMessage;
    private Integer unreadCount;
    private String lastTime;
}
