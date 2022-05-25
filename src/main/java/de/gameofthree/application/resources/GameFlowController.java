package de.gameofthree.application.resources;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import de.gameofthree.application.config.model.StompPrincipal;
import de.gameofthree.application.logic.GameLogic;
import de.gameofthree.application.model.request.MoveRequest;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class GameFlowController {

	private final GameLogic gameLogic;

	@MessageMapping("/play")
	public void makeTheMove(@Payload MoveRequest moveRequest, StompPrincipal stompPrincipal) {
		gameLogic.play(stompPrincipal.getName(), moveRequest.getMove());
	}
}
