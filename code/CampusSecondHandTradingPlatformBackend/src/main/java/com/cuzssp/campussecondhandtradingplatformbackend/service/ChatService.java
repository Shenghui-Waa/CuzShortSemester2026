package com.cuzssp.campussecondhandtradingplatform_backend.service;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.Result;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.*;
import java.util.List;

public interface ChatService {
    Result<List<ChatContactVO>> getContacts(Long userId);
    Result<List<ChatMessageVO>> getMessages(Long userId, Long contactId, Integer page, Integer pageSize);
    Result<ChatMessageVO> sendMessage(Long senderId, Long receiverId, Long productId, String content);
    Result<Void> markAsRead(Long userId, Long contactId);
}
