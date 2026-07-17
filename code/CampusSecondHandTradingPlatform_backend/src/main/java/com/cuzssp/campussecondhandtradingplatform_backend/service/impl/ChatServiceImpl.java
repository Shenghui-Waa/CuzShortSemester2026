package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;

import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.ChatMessage;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatform_backend.common.handler.ChatWebSocketHandler;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToEntityUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToVOUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.ChatMessageMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.UserMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.service.ChatService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.*;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {
    @Autowired private ChatMessageMapper chatMessageMapper;
    @Autowired private UserMapper userMapper;

    @Override
    public Result<List<ChatContactVO>> getContacts(
            Long userId
    ) {
        List<ChatMessage> sent = chatMessageMapper.selectBySenderId(userId);
        List<ChatMessage> received = chatMessageMapper.selectByReceiverId(userId);

        Map<Long, ChatContactVO> contactMap = new HashMap<>();
        for (ChatMessage msg : sent) {
            contactMap.putIfAbsent(msg.getReceiverId(), new ChatContactVO());
            contactMap.get(msg.getReceiverId()).setContactId(msg.getReceiverId());
            contactMap.get(msg.getReceiverId()).setLastMessage(msg.getContent());
        }
        for (ChatMessage msg : received) {
            contactMap.putIfAbsent(msg.getSenderId(), new ChatContactVO());
            ChatContactVO vo = contactMap.get(msg.getSenderId());
            vo.setContactId(msg.getSenderId()); vo.setLastMessage(msg.getContent());
            if (msg.getIsRead() == 0)
                vo.setUnreadCount((vo.getUnreadCount() == null ? 0 : vo.getUnreadCount()) + 1);
        }
        List<ChatContactVO> chatContactVOs = new ArrayList<>();
        for (ChatContactVO chatContactVO : contactMap.values()) {
            User contactedUser = userMapper.selectById(chatContactVO.getContactId());
            if (contactedUser != null) {
                chatContactVO.setContactName(contactedUser.getNickname());
                chatContactVO.setContactAvatar(contactedUser.getAvatar());
            }
            chatContactVOs.add(chatContactVO);
        }
        return Result.success(chatContactVOs);
    }

    @Override
    public Result<List<ChatMessageVO>> getMessages(
            Long userId, Long contactId, Integer page, Integer pageSize
    ) {
        PageHelper.startPage(page, pageSize);
        List<ChatMessage> chatMessageList = chatMessageMapper.selectByConversation(userId, contactId);
        List<ChatMessageVO> chatMessageVOs = chatMessageList.stream()
                .map(ToVOUtil::toChatMessageVO)
                .collect(Collectors.toList());
        Collections.reverse(chatMessageVOs);
        return Result.success(chatMessageVOs);
    }

    @Override
    public Result<ChatMessageVO> sendMessage(
            Long senderId, Long receiverId, Long productId, String content
    ) {
        ChatMessage message = ToEntityUtil.toChatMessageEntity(
                senderId, receiverId, productId, content
        );
        chatMessageMapper.insert(message);
        ChatMessageVO chatMessageVO = ToVOUtil.toChatMessageVO(message);
        ChatWebSocketHandler.sendMessageToUser(receiverId, content);
        return Result.success(chatMessageVO);
    }

    @Override
    public Result<Void> markAsRead(Long userId, Long contactId) {
        chatMessageMapper.markAsRead(userId, contactId);
        return Result.success();
    }
}
