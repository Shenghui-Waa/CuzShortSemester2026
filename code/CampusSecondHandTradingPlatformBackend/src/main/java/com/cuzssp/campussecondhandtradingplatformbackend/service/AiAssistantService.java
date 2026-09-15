package com.cuzssp.campussecondhandtradingplatformbackend.service;

public interface AiAssistantService {

    AiAssistantService.ChatReply chat(String message);

    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    class ChatReply {
        private String reply;
    }
}
