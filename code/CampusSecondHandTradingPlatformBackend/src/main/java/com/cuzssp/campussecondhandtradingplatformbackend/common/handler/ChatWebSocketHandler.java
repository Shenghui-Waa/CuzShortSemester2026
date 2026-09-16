package com.cuzssp.campussecondhandtradingplatformbackend.common.handler;

import com.cuzssp.campussecondhandtradingplatformbackend.common.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final TokenProvider tokenProvider;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = (String) session.getAttributes()
                .get(ChatWebSocketHandshakeInterceptor.TOKEN_ATTRIBUTE);
        Long userId = (Long) session.getAttributes()
                .get(ChatWebSocketHandshakeInterceptor.USER_ID_ATTRIBUTE);
        if (userId != null && tokenProvider.validate(token)) {
            sessions.put(userId, session);
        } else {
            session.close(CloseStatus.POLICY_VIOLATION);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 消息通过HTTP接口发送，WebSocket仅用于推送通知
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = (Long) session.getAttributes()
                .get(ChatWebSocketHandshakeInterceptor.USER_ID_ATTRIBUTE);
        if (userId != null) {
            sessions.remove(userId, session);
        }
    }

    public void sendMessageToUser(Long userId, Long senderId) {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String token = (String) session.getAttributes()
                        .get(ChatWebSocketHandshakeInterceptor.TOKEN_ATTRIBUTE);
                if (!tokenProvider.validate(token)) {
                    sessions.remove(userId, session);
                    session.close(CloseStatus.POLICY_VIOLATION);
                    return;
                }
                String notification = String.format("{\"type\":\"new_message\",\"from\":%d}", senderId);
                session.sendMessage(new TextMessage(notification));
            } catch (Exception e) {
                log.warn("Failed to send message to user {}: {}", userId, e.getMessage());
            }
        }
    }
}
