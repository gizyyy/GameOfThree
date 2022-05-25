package de.gameofthree.application.logic;

import java.util.UUID;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import de.gameofthree.application.model.Game;
import de.gameofthree.application.model.GameRoom;
import de.gameofthree.application.model.Player;
import de.gameofthree.application.model.enums.GameStatusEnum;
import de.gameofthree.application.model.enums.MoveEnum;
import de.gameofthree.application.util.RandomHelper;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GameLogic {

	private GameRoom gameRoom;
	private SimpMessagingTemplate messagingTemplate;

	public void play(String playerTechnicalName, MoveEnum moveEnum) {
		UUID gameId = gameRoom.getPlayers().get(playerTechnicalName);
		Game game = gameRoom.getGames().get(gameId);
		game.getGameStatus();
		if (GameStatusEnum.DONE.equals(game.getGameStatus())) {
			return;
		}
		Player moveOwner = game.getPlayers().stream().filter(p -> playerTechnicalName.equals(p.getTechnicalName()))
				.findFirst().get();

		if (moveOwner.isRobot()) {
			moveEnum = RandomHelper.randomEnum(MoveEnum.class);
		}
		game.executeMove(moveOwner, moveEnum, messagingTemplate);
	}
}
