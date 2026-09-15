package com.cuzssp.campussecondhandtradingplatformbackend.controller;

import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.SendMessageRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.security.TokenProvider;
import com.cuzssp.campussecondhandtradingplatformbackend.service.ChatService;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final TokenProvider tokenProvider;

    /**
     * 获取消息列表
     */
    @GetMapping("/contacts")
    public Result<?> getContacts(
            @RequestHeader("Authorization") String token
    ) {
        Long currentUserId = tokenProvider.getUserId(token);
        return Result.success(chatService.getContacts(currentUserId));
    }

    /**
     * 获取聊天记录
     */
    @GetMapping("/{contactId}")
    public Result<?> getMessages(
            @RequestHeader("Authorization") String token,
            @PathVariable Long contactId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "50") Integer pageSize
    ) {
        Long currentUserId = tokenProvider.getUserId(token);
        return Result.success(chatService.getMessages(currentUserId, contactId, page, pageSize));
    }

    /**
     * 发信息
     */
    @PostMapping("/send")
    public Result<?> sendMessage(
            @RequestHeader("Authorization") String token,
            @RequestBody SendMessageRequest request
    ) {
        Long currentUserId = tokenProvider.getUserId(token);
        return Result.success(chatService.sendMessage(
                currentUserId, request.getReceiverId(),
                request.getProductId(), request.getContent()
        ));
    }

    /**
     * 标记已读
     */
    @PutMapping("/read/{contactId}")
    public Result<?> markAsRead(
            @RequestHeader("Authorization") String token,
            @PathVariable Long contactId
    ) {
        Long currentUserId = tokenProvider.getUserId(token);
        return Result.success(chatService.markAsRead(
                currentUserId,
                contactId
        ));
    }

}
