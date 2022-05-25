package de.gameofthree.application.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import de.gameofthree.application.model.enums.GameStatusEnum;
import de.gameofthree.application.model.enums.MoveEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Game {
	private GameStatusEnum gameStatus;
	private List<Player> players;
	private int currentNumber;
	private UUID gameId;

	public boolean isWaitingForPlayer() {
		return this.gameStatus == GameStatusEnum.PLAYER_MISSING;
	}

	public void setGameStatus(Player lastPlayed) {
		if (lastPlayed.isPlayerOne()) {
			this.setGameStatus(GameStatusEnum.PLAYER2S_TURN);
		} else {
			this.setGameStatus(GameStatusEnum.PLAYER1S_TURN);
		}
	}

	public void setGameStatus(GameStatusEnum gameStatusEnum) {
		this.gameStatus = gameStatusEnum;
	}

	public void start(final SimpMessagingTemplate messagingTemplate) {
		Optional<Player> playerOne = this.getPlayers().stream().filter(Player::isPlayerOne).findFirst();
		this.setGameStatus(GameStatusEnum.PLAYER1S_TURN);
		this.getPlayers().stream().forEach(p -> {
			p.announceWelcome(messagingTemplate);
		});
		executeMove(playerOne.get(), MoveEnum.INPUT, messagingTemplate);
		messagingTemplate.convertAndSend("/topic/logs", generateMatchMessage());
	}

	public void executeMove(final Player player, final MoveEnum comingMove,
			final SimpMessagingTemplate messagingTemplate) {
		Integer lastNumber = comingMove.play(this.getCurrentNumber());
		player.setLastMove(new Move(lastNumber, comingMove.getNumber()));
		setCurrentNumber(lastNumber);
		String moveMessage = player.generateMoveMessage();
		setGameStatus(player);
		messagingTemplate.convertAndSend("/topic/logs", player.generateMoveMessage());
		this.getPlayers().stream().forEach(p -> {
			p.announcePlayerMoved(messagingTemplate, this.getGameStatus(), moveMessage);
			if (this.currentNumber <= 1) {
				this.setGameStatus(GameStatusEnum.DONE);
				p.announceWinner(messagingTemplate, player);
			}
		});

	}

	private String generateMatchMessage() {
		return String.format("%s joined and matched with %s", this.getPlayers().get(1).getName(),
				this.getPlayers().get(0).getName());
	}
}
