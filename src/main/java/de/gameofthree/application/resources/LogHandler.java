package de.gameofthree.application.resources;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class LogHandler {

	@MessageMapping("/log")
	public void makeTheMove(String message) {
		log.info(message);
	}
}
