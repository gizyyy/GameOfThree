package de.gameofthree.application.logic.event;

import java.io.Serializable;
import java.util.UUID;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameStartedEvent extends ApplicationEvent implements Serializable {

	private static final long serialVersionUID = 1L;
	private UUID gameKey;

	GameStartedEvent(UUID gameKey) {
		super(GameStartedEvent.class);
		this.gameKey = gameKey;
	}

}