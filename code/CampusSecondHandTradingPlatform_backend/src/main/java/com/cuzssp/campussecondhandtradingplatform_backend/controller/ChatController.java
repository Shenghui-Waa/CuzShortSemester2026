package com.cuzssp.campussecondhandtradingplatform_backend.controller;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.SendMessageRequest;

import com.cuzssp.campussecondhandtradingplatform_backend.common.security.JwtTokenProvider;
import com.cuzssp.campussecondhandtradingplatform_backend.service.ChatService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/contacts")
    public Result<?> getContacts(@RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        return chatService.getContacts(userId);
    }

    @GetMapping("/{contactId}")
    public Result<?> getMessages(@RequestHeader("Authorization") String token,
                                  @PathVariable Long contactId,
                                  @RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "50") Integer pageSize) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        return chatService.getMessages(userId, contactId, page, pageSize);
    }

    @PostMapping("/send")
    public Result<?> sendMessage(@RequestHeader("Authorization") String token,
                                  @RequestBody SendMessageRequest request) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        return chatService.sendMessage(userId, request.getReceiverId(), request.getProductId(), request.getContent());
    }

    @PutMapping("/read/{contactId}")
    public Result<?> markAsRead(@RequestHeader("Authorization") String token,
                                 @PathVariable Long contactId) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        return chatService.markAsRead(userId, contactId);
    }
}
