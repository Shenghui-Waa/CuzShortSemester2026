package com.cuzssp.campussecondhandtradingplatform_backend.controller;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.SendMessageRequest;

import com.cuzssp.campussecondhandtradingplatform_backend.common.security.JwtTokenProvider;
import com.cuzssp.campussecondhandtradingplatform_backend.service.ChatService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final JwtTokenProvider jwtTokenProvider;


    @GetMapping("/contacts")
    public Result<?> getContacts(
            @RequestHeader("Authorization") String token
    ) {
        Long currentUserId = getCurrentUserId(token);
        return chatService.getContacts(currentUserId);
    }

    @GetMapping("/{contactId}")
    public Result<?> getMessages(
            @RequestHeader("Authorization") String token,
            @PathVariable Long contactId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "50") Integer pageSize
    ) {
        Long currentUserId = getCurrentUserId(token);
        return chatService.getMessages(currentUserId, contactId, page, pageSize);
    }

    @PostMapping("/send")
    public Result<?> sendMessage(
            @RequestHeader("Authorization") String token,
            @RequestBody SendMessageRequest request
    ) {
        Long currentUserId = getCurrentUserId(token);
        return chatService.sendMessage(
                currentUserId, request.getReceiverId(),
                request.getProductId(), request.getContent()
        );
    }

    @PutMapping("/read/{contactId}")
    public Result<?> markAsRead(
            @RequestHeader("Authorization") String token,
            @PathVariable Long contactId
    ) {
        Long currentUserId = getCurrentUserId(token);
        return chatService.markAsRead(
                currentUserId,
                contactId
        );
    }

    private Long getCurrentUserId(String token) {
        if (token == null || token.isEmpty())
            return null;
        return jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));

    }
}
