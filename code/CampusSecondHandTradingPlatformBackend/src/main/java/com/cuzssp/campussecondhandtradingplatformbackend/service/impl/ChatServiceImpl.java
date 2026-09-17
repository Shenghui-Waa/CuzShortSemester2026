package com.cuzssp.campussecondhandtradingplatformbackend.service.impl;

import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.ChatMessageConstant;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.Result;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.ChatMessageRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.ChatMessage;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatformbackend.common.handler.ChatWebSocketHandler;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.AesEncryptionUtil;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.ToEntityUtil;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.ToVOUtil;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.ChatMessageMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.UserMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.service.ChatService;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.ChatMessageVO;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.ChatContactVO;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.cuzssp.campussecondhandtradingplatformbackend.common.exception.BusinessException;

import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Objects;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageMapper chatMessageMapper;
    private final UserMapper userMapper;
    private final AesEncryptionUtil aesEncryptionUtil;
    private final ChatWebSocketHandler chatWebSocketHandler;

    // 获取消息列表
    @Override
    public List<ChatContactVO> getContacts(
            Long userId
    ) {
        List<ChatMessage> messages = chatMessageMapper.selectByUserId(userId);
        Map<Long, ChatContactVO> contactMap = new LinkedHashMap<>();
        for (ChatMessage msg : messages) {
            // 发信人
            if (Objects.equals(msg.getSenderId(), userId)) {
                contactMap.putIfAbsent(msg.getReceiverId(), new ChatContactVO());
                ChatContactVO chatContactVO = contactMap.get(msg.getReceiverId());
                chatContactVO.setContactId(msg.getReceiverId());
                if (chatContactVO.getLastMessage() == null) {
                    chatContactVO.setLastMessage(aesEncryptionUtil.decrypt(msg.getContent()));
                    chatContactVO.setLastTime(msg.getCreatedAt()
                            .toString());
                }
            }
            // 收信人
            if (Objects.equals(msg.getReceiverId(), userId)) {
                contactMap.putIfAbsent(msg.getSenderId(), new ChatContactVO());
                ChatContactVO chatContactVO = contactMap.get(msg.getSenderId());
                chatContactVO.setContactId(msg.getSenderId());
                if (chatContactVO.getLastMessage() == null) {
                    chatContactVO.setLastMessage(aesEncryptionUtil.decrypt(msg.getContent()));
                    chatContactVO.setLastTime(msg.getCreatedAt()
                            .toString());
                }
                if (Objects.equals(msg.getIsRead(), ChatMessageConstant.ReadStatus.NO)) {
                    chatContactVO.setUnreadCount((chatContactVO.getUnreadCount() == null
                            ? 0 : chatContactVO.getUnreadCount()) + 1);
                }
            }
        }

        List<ChatContactVO> chatContactVOs = new ArrayList<>();
        for (ChatContactVO chatContactVO : contactMap.values()) {
            if (chatContactVO.getUnreadCount() == null) {
                chatContactVO.setUnreadCount(0);
            }
            User contactedUser = userMapper.selectById(chatContactVO.getContactId());
            if (contactedUser != null) {
                chatContactVO.setContactName(contactedUser.getNickname());
                chatContactVO.setContactAvatar(contactedUser.getAvatar());
                chatContactVO.setContactUsername(contactedUser.getUsername());
            }
            chatContactVOs.add(chatContactVO);
        }

        return chatContactVOs;
    }

    // 获取聊天记录
    @Override
    public List<ChatMessageVO> getMessages(
            Long userId, Long contactId, Integer page, Integer pageSize
    ) {
        if (page == null || page < 1
                || pageSize == null || pageSize < 1 || pageSize > 100)
            throw new BusinessException("无效的分页");

        List<ChatMessage> chatMessageList;
        try {
            PageHelper.startPage(page, pageSize);
            chatMessageList = chatMessageMapper.selectByConversation(userId, contactId);
        } finally {
            PageHelper.clearPage();
        }
        List<ChatMessageVO> chatMessageVOs = chatMessageList
                .stream()
                .map(ToVOUtil::toChatMessageVO)
                .collect(Collectors.toList());
        for (ChatMessageVO chatMessageVO : chatMessageVOs) {
            chatMessageVO.setContent(aesEncryptionUtil.decrypt(chatMessageVO.getContent()));
        }
        Collections.reverse(chatMessageVOs);
        return chatMessageVOs;
    }

    // 发送信息
    @Override
    public ChatMessageVO sendMessage(
            Long senderId, ChatMessageRequest request
    ) {
        if (userMapper.selectById(request.getReceiverId()) == null)
            throw new BusinessException(Result.Code.NOT_FOUND, "接收者不存在");

        ChatMessage message = ToEntityUtil.toChatMessageEntity(senderId, request);
        message.setContent(aesEncryptionUtil.encrypt(request.getContent()));
        chatMessageMapper.insert(message);
        ChatMessageVO chatMessageVO = ToVOUtil.toChatMessageVO(message);
        chatMessageVO.setContent(request.getContent());

        chatWebSocketHandler.sendMessageToUser(request.getReceiverId(), senderId);

        return chatMessageVO;
    }

    // 标记已读
    @Override
    public Void markAsRead(Long userId, Long contactId) {
        chatMessageMapper.markAsRead(userId, contactId);
        return null;
    }
}
