package com.cuzssp.campussecondhandtradingplatformbackend.service.impl;

import com.cuzssp.campussecondhandtradingplatformbackend.common.handler.ChatWebSocketHandler;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.AesEncryptionUtil;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.ChatMessageMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.UserMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageMapper chatMessageMapper;
    private final UserMapper userMapper;
    private final AesEncryptionUtil aesEncryptionUtil;
    private final ChatWebSocketHandler chatWebSocketHandler;

}
