package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.ChatMessage;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.ChatMessageMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.UserMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.service.ChatService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {
    @Autowired private ChatMessageMapper chatMessageMapper;
    @Autowired private UserMapper userMapper;

    @Override
    public Result<List<ChatContactVO>> getContacts(Long userId) {
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
        List<ChatContactVO> contacts = new ArrayList<>();
        for (ChatContactVO vo : contactMap.values()) {
            User c = userMapper.selectById(vo.getContactId());
            if (c != null) { vo.setContactName(c.getNickname()); vo.setContactAvatar(c.getAvatar()); }
            contacts.add(vo);
        }
        return Result.success(contacts);
    }

    @Override
    public Result<List<ChatMessageVO>> getMessages(Long userId, Long contactId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<ChatMessage> pg = chatMessageMapper.selectByConversation(userId, contactId);
        List<ChatMessageVO> vos = pg.stream().map(msg -> {
            ChatMessageVO vo = new ChatMessageVO();
            vo.setId(msg.getId()); vo.setSenderId(msg.getSenderId()); vo.setReceiverId(msg.getReceiverId());
            vo.setProductId(msg.getProductId()); vo.setContent(msg.getContent());
            vo.setIsRead(msg.getIsRead()); vo.setCreatedAt(msg.getCreatedAt());
            return vo;
        }).collect(Collectors.toList());
        Collections.reverse(vos);
        return Result.success(vos);
    }

    @Override
    public Result<ChatMessageVO> sendMessage(Long senderId, Long receiverId, Long productId, String content) {
        ChatMessage msg = new ChatMessage();
        msg.setSenderId(senderId); msg.setReceiverId(receiverId);
        msg.setProductId(productId); msg.setContent(content); msg.setIsRead(0);
        chatMessageMapper.insert(msg);
        ChatMessageVO vo = new ChatMessageVO();
        vo.setId(msg.getId()); vo.setSenderId(msg.getSenderId()); vo.setReceiverId(msg.getReceiverId());
        vo.setProductId(msg.getProductId()); vo.setContent(msg.getContent());
        vo.setIsRead(msg.getIsRead()); vo.setCreatedAt(msg.getCreatedAt());
        return Result.success(vo);
    }

    @Override
    public Result<Void> markAsRead(Long userId, Long contactId) {
        chatMessageMapper.markAsRead(userId, contactId);
        return Result.success();
    }
}
