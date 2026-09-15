package com.cuzssp.campussecondhandtradingplatformbackend.service;

import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.ChatMessageVO;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.ChatContactVO;
import java.util.List;

public interface ChatService {
    List<ChatContactVO> getContacts(Long userId);
    List<ChatMessageVO> getMessages(Long userId, Long contactId, Integer page, Integer pageSize);
    ChatMessageVO sendMessage(Long senderId, Long receiverId, Long productId, String content);
    Void markAsRead(Long userId, Long contactId);
}
