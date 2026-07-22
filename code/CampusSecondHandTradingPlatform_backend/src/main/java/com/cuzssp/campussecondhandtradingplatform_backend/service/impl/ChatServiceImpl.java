package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;

import com.cuzssp.campussecondhandtradingplatform_backend.common.constant.ChatMessageConstant;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.ChatMessage;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatform_backend.common.handler.ChatWebSocketHandler;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.AesEncryptionUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToEntityUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToVOUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.ChatMessageMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.UserMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.service.ChatService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.*;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageMapper chatMessageMapper;
    private final UserMapper userMapper;
    private final AesEncryptionUtil aesEncryptionUtil;
    private final ChatWebSocketHandler chatWebSocketHandler;

    @Override
    public Result<List<ChatContactVO>> getContacts(
            Long userId
    ) {
        List<ChatMessage> messages = chatMessageMapper.selectByUserId(userId);
        Map<Long, ChatContactVO> contactMap = new HashMap<>();
        for (ChatMessage msg : messages) {
            // 发信人
            if (msg.getSenderId().equals(userId)) {
                contactMap.putIfAbsent(msg.getReceiverId(), new ChatContactVO());
                ChatContactVO chatContactVO = contactMap.get(msg.getReceiverId());
                chatContactVO.setContactId(msg.getReceiverId());
                if (chatContactVO.getLastMessage() == null) {
                    chatContactVO.setLastMessage(aesEncryptionUtil.decrypt(msg.getContent()));
                    chatContactVO.setLastTime(msg.getCreatedAt().toString());
                }
            }
            // 收信人
            if (msg.getReceiverId().equals(userId)) {
                contactMap.putIfAbsent(msg.getSenderId(), new ChatContactVO());
                ChatContactVO chatContactVO = contactMap.get(msg.getSenderId());
                chatContactVO.setContactId(msg.getSenderId());
                if (chatContactVO.getLastMessage() == null) {
                    chatContactVO.setLastMessage(aesEncryptionUtil.decrypt(msg.getContent()));
                    chatContactVO.setLastTime(msg.getCreatedAt().toString());
                }
                if (msg.getIsRead() == ChatMessageConstant.READ_STATUS_NO)
                    chatContactVO.setUnreadCount((chatContactVO.getUnreadCount() == null
                            ? 0 : chatContactVO.getUnreadCount()) + 1);
            }
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
        for (ChatMessageVO chatMessageVO : chatMessageVOs) {
            chatMessageVO.setContent(aesEncryptionUtil.decrypt(chatMessageVO.getContent()));
        }
        Collections.reverse(chatMessageVOs);
        return Result.success(chatMessageVOs);
    }

    @Override
    public Result<ChatMessageVO> sendMessage(
            Long senderId, Long receiverId, Long productId, String content
    ) {
        String encryptedContent = aesEncryptionUtil.encrypt(content);
        ChatMessage message = ToEntityUtil.toChatMessageEntity(
                senderId, receiverId, productId, encryptedContent);
        chatMessageMapper.insert(message);
        ChatMessageVO chatMessageVO = ToVOUtil.toChatMessageVO(message);
        chatMessageVO.setContent(content);

        chatWebSocketHandler.sendMessageToUser(receiverId, senderId);

        return Result.success(chatMessageVO);
    }

    @Override
    public Result<Void> markAsRead(Long userId, Long contactId) {
        chatMessageMapper.markAsRead(userId, contactId);
        return Result.success();
    }
}
