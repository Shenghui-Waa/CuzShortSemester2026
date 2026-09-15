package com.cuzssp.campussecondhandtradingplatform_backend.common.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AnnouncementVO {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
