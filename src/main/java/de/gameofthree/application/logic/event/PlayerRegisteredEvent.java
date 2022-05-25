package de.gameofthree.application.logic.event;

import java.io.Serializable;

import org.springframework.context.ApplicationEvent;

import de.gameofthree.application.model.Player;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerRegisteredEvent extends ApplicationEvent implements Serializable {

	private static final long serialVersionUID = 1L;
	private Player player;

	PlayerRegisteredEvent(Player player) {
		super(PlayerRegisteredEvent.class);
		this.player = player;
	}

}