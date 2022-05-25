package de.gameofthree.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ContextConfiguration;

import de.gameofthree.application.GameOfThreeApplication;
import de.gameofthree.application.model.Game;
import de.gameofthree.application.model.GameRoom;
import de.gameofthree.application.model.Player;
import de.gameofthree.application.model.enums.GameStatusEnum;
import de.gameofthree.application.model.enums.MoveEnum;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = GameOfThreeApplication.class)
class GameFlowControllerTest {

	@MockBean
	private GameRoom gameRoom;

	@MockBean
	private SimpMessagingTemplate simpMessagingTemplate;

	private Player player;

	private Game game;

	@BeforeEach
	void setUp() {
		ConcurrentHashMap<UUID, Game> mockGames = new ConcurrentHashMap<UUID, Game>();
		List<Player> players = new ArrayList<Player>();
		player = new Player("Mike", "Mike", false);
		Player player2 = new Player("Tuna", "Tuna", false);
		players.add(player);
		players.add(player2);
		UUID mockId = UUID.randomUUID();
		game = new Game(GameStatusEnum.PLAYER2S_TURN, players, 19, UUID.randomUUID());
		mockGames.put(mockId, game);

		ConcurrentHashMap<String, UUID> playerMap = new ConcurrentHashMap<String, UUID>();
		playerMap.put("Mike", mockId);
		playerMap.put("Tuna", mockId);
		gameRoom.setPlayers(playerMap);
		Mockito.when(gameRoom.getPlayers()).thenReturn(playerMap);
		Mockito.when(gameRoom.getGames()).thenReturn(mockGames);
	}

	@Test
	public void shouldBeAbleToMove() {
		game.executeMove(player, MoveEnum.MINUSONE, simpMessagingTemplate);
		assertEquals(game.getCurrentNumber(), 6);
		assertEquals(game.getGameStatus(), GameStatusEnum.PLAYER1S_TURN);
	}

	@Test
	public void shouldBeAbleToFinishGame() {
		game.setCurrentNumber(4);
		game.executeMove(player, MoveEnum.MINUSONE, simpMessagingTemplate);
		assertEquals(game.getCurrentNumber(), 1);
		assertEquals(game.getGameStatus(), GameStatusEnum.DONE);
	}

}