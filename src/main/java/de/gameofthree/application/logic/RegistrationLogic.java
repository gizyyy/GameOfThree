package de.gameofthree.application.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import de.gameofthree.application.logic.event.PlayerRegisteredEvent;
import de.gameofthree.application.model.Game;
import de.gameofthree.application.model.GameRoom;
import de.gameofthree.application.model.Player;
import de.gameofthree.application.model.enums.GameStatusEnum;
import de.gameofthree.application.model.enums.PlayerRoleEnum;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RegistrationLogic {

	private SimpMessagingTemplate messagingTemplate;
	private GameRoom gameRoom;

	@EventListener
	@Async
	public void registerPlayer(PlayerRegisteredEvent event) {
		Optional<Game> findFirst = gameRoom.getGames().values().stream().filter(Game::isWaitingForPlayer).findFirst();
		if (findFirst.isEmpty()) {
			registerNewPlayerToWaitingList(event.getPlayer());
			return;
		}
		registerPlayerToWaitingGame(event, findFirst.get());

	}

	private void registerPlayerToWaitingGame(PlayerRegisteredEvent event, Game game) {
		UUID keyGame = gameRoom.getGames().entrySet().stream().filter(entry -> game.equals(entry.getValue()))
				.findFirst().map(Map.Entry::getKey).orElse(null);
		Player player = event.getPlayer();
		player.setRole(PlayerRoleEnum.PLAYER2);
		game.getPlayers().add(player);
		gameRoom.getPlayers().put(player.getTechnicalName(), keyGame);
		game.start(messagingTemplate);
	}

	private void registerNewPlayerToWaitingList(final Player player) {
		player.setRole(PlayerRoleEnum.PLAYER1);
		List<Player> playerList = new ArrayList<Player>();
		playerList.add(player);
		UUID randomUUID = UUID.randomUUID();
		Game game = new Game(GameStatusEnum.PLAYER_MISSING, playerList, 0, randomUUID);
		gameRoom.getGames().put(randomUUID, game);
		gameRoom.getPlayers().put(player.getTechnicalName(), randomUUID);
		messagingTemplate.convertAndSend("/topic/logs",
				String.format("%s waiting for someone to join...", player.getName()));
	}
}
