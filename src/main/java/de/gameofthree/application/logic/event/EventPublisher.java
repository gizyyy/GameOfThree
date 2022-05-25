package de.gameofthree.application.logic.event;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import de.gameofthree.application.model.Player;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class EventPublisher {

	private final ApplicationEventPublisher publisher;

	public void publishUserRegisteredEvent(final Player player) {
		publisher.publishEvent(new PlayerRegisteredEvent(player));
	}

	public void publishGameStartedEvent(final UUID gameIdentifier) {
		publisher.publishEvent(new GameStartedEvent(gameIdentifier));
	}

}
