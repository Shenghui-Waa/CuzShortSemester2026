package com.cuzssp.campussecondhandtradingplatform_backend.common.handler;

import com.cuzssp.campussecondhandtradingplatform_backend.common.security.JwtTokenProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final JwtTokenProvider jwtTokenProvider;

    public ChatWebSocketHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = getUserId(session);
        if (userId != null) {
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
        Long userId = getUserId(session);
        if (userId != null) {
            sessions.remove(userId);
        }
    }

    public void sendMessageToUser(Long userId, Long senderId) {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String notification = String.format("{\"type\":\"new_message\",\"from\":%d}", senderId);
                session.sendMessage(new TextMessage(notification));
            } catch (Exception e) {
                // ignore
            }
        }
    }

    private Long getUserId(WebSocketSession session) {
        String query = session.getUri() != null ? session.getUri().getQuery() : null;
        if (query != null && query.contains("token=")) {
            try {
                String token = query.split("token=")[1].split("&")[0];
                if (jwtTokenProvider.validateToken(token)) {
                    return jwtTokenProvider.getUserIdFromToken(token);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}