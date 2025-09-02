package com.bgmi.config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

@Component
public class PlayerPresenceService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ConcurrentMap<String, Boolean> sessions = new ConcurrentHashMap<>();

    public PlayerPresenceService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        if (sessionId != null) {
            sessions.put(sessionId, Boolean.TRUE);
            broadcastPlayerCount();
        }
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        if (sessionId != null) {
            sessions.remove(sessionId);
            broadcastPlayerCount();
        }
    }

    private void broadcastPlayerCount() {
        int count = sessions.size();
        messagingTemplate.convertAndSend("/topic/players", Map.of("count", count));
        System.out.println("Broadcasted player count: " + count);
    }

    // REST helper
    public int getOnlineCount() {
        return sessions.size();
    }

    // 🔥 New: allow clients to request count on demand
    @MessageMapping("/requestPlayerCount")
    public void handlePlayerCountRequest() {
        broadcastPlayerCount();
    }
}
