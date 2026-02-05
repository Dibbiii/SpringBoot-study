package com.example.gem_springboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Abilita il broker di messaggi
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Abilita un broker in memoria semplice
        // I client si iscriveranno a topic che iniziano con "/topic"
        config.enableSimpleBroker("/topic");

        // Prefisso per i messaggi che il client invia al server (non lo useremo subito ma è standard)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Questo è l'endpoint HTTP dove Angular farà l'handshake iniziale per connettersi.
        // Importante: setAllowedOriginPatterns("*") per evitare problemi CORS in sviluppo.
        registry.addEndpoint("/ws-gem").setAllowedOriginPatterns("*");
        // .withSockJS(); // Abilita SockJS (fallback se il browser non supporta WebSocket puri)
    }
}
