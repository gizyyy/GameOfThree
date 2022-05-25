package de.gameofthree.test;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import de.gameofthree.application.GameOfThreeApplication;
import de.gameofthree.application.model.request.RegisterRequest;
import lombok.SneakyThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = GameOfThreeApplication.class)
class RegisterControllerTest {

	@LocalServerPort
	private Integer port;

	private WebSocketStompClient webSocketStompClient;

	@BeforeEach
	public void setup() {
		this.webSocketStompClient = new WebSocketStompClient(
				new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))));

		this.webSocketStompClient.setMessageConverter(new CompositeMessageConverter(
				List.of(new StringMessageConverter(), new MappingJackson2MessageConverter())));
	}

	@Test
	@SneakyThrows
	public void shouldRegisterPlayerToWaitingListWhenThereIsNoWaitingGame() {
		Entry<StompSession, BlockingQueue<String>> establishConnection = establishConnection();
		establishConnection.getKey().send("/app/register", new RegisterRequest("Mike", false));
		assertEquals("Mike waiting for someone to join...", establishConnection.getValue().poll(5, SECONDS));
	}

	@Test
	@SneakyThrows
	public void shouldRegisterPlayerToGameWhenThereIsWaitingGame() {
		Entry<StompSession, BlockingQueue<String>> establishConnection = establishConnection();
		establishConnection.getKey().send("/app/register", new RegisterRequest("Mike", false));
		// give sometime because reciever is async
		Thread.sleep(1000);
		establishConnection.getKey().send("/app/register", new RegisterRequest("Tuna", false));

		assertEquals("Mike waiting for someone to join...", establishConnection.getValue().take());
		Assertions.assertThat(establishConnection.getValue().take()).startsWith("Move from Mike with number");
		assertEquals("Tuna joined and matched with Mike", establishConnection.getValue().take());
	}

	@SneakyThrows
	private Map.Entry<StompSession, BlockingQueue<String>> establishConnection() {
		BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<String>(10);
		StompSession session = webSocketStompClient.connect(getWsPath(), new StompSessionHandlerAdapter() {
			@Override
			public void handleException(StompSession session, StompCommand command, StompHeaders headers,
					byte[] payload, Throwable exception) {
				throw new RuntimeException("Failure in WebSocket handling", exception);
			}
		}).get(550, SECONDS);

		session.subscribe("/topic/logs", new StompFrameHandler() {
			@Override
			public Type getPayloadType(StompHeaders headers) {
				return String.class;
			}

			@Override
			public void handleFrame(StompHeaders headers, Object payload) {
				System.out.println("Received message: " + payload);
				blockingQueue.add((String) payload);
			}
		});

		return Map.entry(session, blockingQueue);
	}

	private String getWsPath() {
		return String.format("ws://localhost:%d/gs-guide-websocket", port);
	}
}