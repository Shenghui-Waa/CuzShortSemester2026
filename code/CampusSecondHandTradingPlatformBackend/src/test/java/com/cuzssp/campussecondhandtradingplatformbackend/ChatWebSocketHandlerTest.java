package com.cuzssp.campussecondhandtradingplatformbackend;

import com.cuzssp.campussecondhandtradingplatformbackend.common.handler.ChatWebSocketHandler;
import com.cuzssp.campussecondhandtradingplatformbackend.common.handler.ChatWebSocketHandshakeInterceptor;
import com.cuzssp.campussecondhandtradingplatformbackend.common.security.TokenProvider;
import com.cuzssp.campussecondhandtradingplatformbackend.common.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

class ChatWebSocketHandlerTest {

    @Test
    void invalidTokenStopsPushAndClosesSession() throws Exception {
        TokenProvider tokenProvider = mock(TokenProvider.class);
        ChatWebSocketHandler handler = new ChatWebSocketHandler(tokenProvider);
        WebSocketSession session = mock(WebSocketSession.class);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(ChatWebSocketHandshakeInterceptor.USER_ID_ATTRIBUTE, 7L);
        attributes.put(ChatWebSocketHandshakeInterceptor.TOKEN_ATTRIBUTE, "test-token");
        when(session.getAttributes()).thenReturn(attributes);
        when(session.isOpen()).thenReturn(true);
        when(tokenProvider.validate("test-token")).thenReturn(true, false);

        handler.afterConnectionEstablished(session);
        handler.sendMessageToUser(7L, 9L);

        verify(session, never()).sendMessage(any(TextMessage.class));
        verify(session).close(CloseStatus.POLICY_VIOLATION);
    }

    @Test
    void invalidTokenIsRejectedBeforeHandshake() {
        TokenProvider tokenProvider = mock(TokenProvider.class);
        ChatWebSocketHandshakeInterceptor interceptor =
                new ChatWebSocketHandshakeInterceptor(tokenProvider);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        WebSocketHandler handler = mock(WebSocketHandler.class);
        Map<String, Object> attributes = new HashMap<>();

        when(request.getURI())
                .thenReturn(URI.create("ws://localhost/ws/chat?token=test-token"));
        when(tokenProvider.getUserId("test-token"))
                .thenThrow(new BusinessException("Invalid token"));

        boolean accepted = interceptor.beforeHandshake(
                request,
                response,
                handler,
                attributes
        );

        assertThat(accepted).isFalse();
        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
        assertThat(attributes).isEmpty();
    }
}
