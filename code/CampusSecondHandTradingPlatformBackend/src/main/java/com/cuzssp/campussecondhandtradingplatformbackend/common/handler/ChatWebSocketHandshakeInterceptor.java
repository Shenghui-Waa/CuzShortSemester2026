package com.cuzssp.campussecondhandtradingplatformbackend.common.handler;

import com.cuzssp.campussecondhandtradingplatformbackend.common.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandshakeInterceptor implements HandshakeInterceptor {

    public static final String USER_ID_ATTRIBUTE = "userId";
    public static final String TOKEN_ATTRIBUTE = "token";

    private final TokenProvider tokenProvider;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        String token = getToken(request);
        try {
            Long userId = tokenProvider.getUserId(token);
            attributes.put(USER_ID_ATTRIBUTE, userId);
            attributes.put(TOKEN_ATTRIBUTE, token);
            return true;
        } catch (Exception exception) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {
        // 无需额外处理
    }

    private String getToken(ServerHttpRequest request) {
        String query = request.getURI().getQuery();
        if (query == null) return null;
        for (String parameter : query.split("&")) {
            if (parameter.startsWith("token=")) {
                return parameter.substring("token=".length());
            }
        }
        return null;
    }
}
