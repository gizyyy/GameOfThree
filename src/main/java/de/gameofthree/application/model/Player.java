package de.gameofthree.application.model;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import de.gameofthree.application.logic.event.EventPublisher;
import de.gameofthree.application.model.enums.GameStatusEnum;
import de.gameofthree.application.model.enums.PlayerRoleEnum;
import de.gameofthree.application.model.response.GameFlowResponse;
import de.gameofthree.application.model.response.GameStartedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Player {
	private String name;
	private String technicalName;
	private PlayerRoleEnum role;
	private Move lastMove;
	private boolean robot;

	public Player(final String name, final String technicalName, final boolean robot) {
		this.name = name;
		this.technicalName = technicalName;
		this.robot = robot;
	}

	public boolean isPlayerOne() {
		return PlayerRoleEnum.PLAYER1.equals(this.role);
	}

	public void announcePlayerRegistered(final EventPublisher eventPublisher) {
		eventPublisher.publishUserRegisteredEvent(this);
	}

	public void announcePlayerMoved(final SimpMessagingTemplate messagingTemplate, final GameStatusEnum gameStatusEnum,
			final String message) {
		GameFlowResponse gameStartedResponse = new GameFlowResponse(gameStatusEnum.toString(), message);
		messagingTemplate.convertAndSendToUser(this.getTechnicalName(), "/queue/special", gameStartedResponse);
	}

	public void announceWinner(final SimpMessagingTemplate messagingTemplate, final Player winner) {
		String message = String.format("%s won", winner.getName());
		messagingTemplate.convertAndSendToUser(this.getTechnicalName(), "/queue/special",
				new GameFlowResponse(GameStatusEnum.DONE.toString(), message));
	}

	public void announceWelcome(final SimpMessagingTemplate messagingTemplate) {
		messagingTemplate.convertAndSendToUser(this.getTechnicalName(), "/queue/special", new GameStartedResponse(
				this.getName(), GameStatusEnum.PLAYER2S_TURN.toString(), this.getRole().getWelcomeText()));
	}

	public String generateMoveMessage() {
		return String.format("Move from %s with number %s, and current value is %s", this.getName(),
				this.getLastMove().getMoveNumber(), this.getLastMove().getAfterMoveNumber());
	}

}
