package net.acesinc.data.json.generator.socket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/*
 * * @EnableWebSocketMessageBroker annotation enables broker implementation for web socket. 
   * By default, spring uses in-memory broker using STOMP. 
   * But Spring exposes easy way of replacing to RabbitMQ, ActiveMQ broker etc.*/
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	
    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
    	/*addEndpoint(“/websocket-chat”) – Register a STOMP over WebSocket endpoint at the given mapping path.
    	 * All the websocket request must start with /websocket-chat i.e. [http://localhost:8080/websocket-chat/]
    	 * (8080 is spring service port number)*/
        stompEndpointRegistry.addEndpoint("/output")
                .setAllowedOrigins("*")
                .withSockJS();
    }
	    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic/");
        registry.setApplicationDestinationPrefixes("/app");
    }
}

