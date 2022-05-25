package de.gameofthree.application.model.request;

import de.gameofthree.application.model.enums.MoveEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MoveRequest {
	private MoveEnum move;

	public void setMove(String move) {
		this.move = MoveEnum.fromString(move);
	}

}
