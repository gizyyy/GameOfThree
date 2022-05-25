package de.gameofthree.application.model;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class GameRoom {

	private ConcurrentHashMap<UUID, Game> games = new ConcurrentHashMap<UUID, Game>();
	private ConcurrentHashMap<String, UUID> players = new ConcurrentHashMap<String, UUID>();
}
