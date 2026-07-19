package com.cuzssp.campussecondhandtradingplatform_backend.service;

import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;

public interface AiAssistantService {

    Result<AiAssistantService.ChatReply> chat(String message);

    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    class ChatReply {
        private String reply;
    }
}
