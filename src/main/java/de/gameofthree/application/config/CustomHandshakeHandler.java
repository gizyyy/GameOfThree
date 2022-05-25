package de.gameofthree.application.config;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import de.gameofthree.application.config.model.StompPrincipal;

@Component
public class CustomHandshakeHandler extends DefaultHandshakeHandler {

	@Override
	protected Principal determineUser(org.springframework.http.server.ServerHttpRequest request,
			WebSocketHandler wsHandler, Map<String, Object> attributes) {
		
		return new StompPrincipal(UUID.randomUUID().toString());
	}
}