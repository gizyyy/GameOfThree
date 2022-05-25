package de.gameofthree.application.resources;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import de.gameofthree.application.config.model.StompPrincipal;
import de.gameofthree.application.logic.event.EventPublisher;
import de.gameofthree.application.model.Player;
import de.gameofthree.application.model.request.RegisterRequest;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class GameRegistrationController {

	private final EventPublisher eventPublisher;

	@MessageMapping("/register")
	public Player registerToGame(@Payload RegisterRequest registerRequest, StompPrincipal stompPrincipal) {
		Player player = new Player(registerRequest.getName(), stompPrincipal.getName(), registerRequest.isRobot());
		player.announcePlayerRegistered(eventPublisher);
		return player;
	}

}
