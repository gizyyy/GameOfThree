package de.gameofthree.application.model.enums;

public enum MoveEnum {
	MINUSONE {

		@Override
		public Integer play(Integer coming) {
			return divide(coming + getNumber());
		}

		@Override
		public Integer getNumber() {
			return -1;
		}
	},
	ZERO {

		@Override
		public Integer play(Integer coming) {
			return divide(coming + getNumber());
		}

		@Override
		public Integer getNumber() {
			return 0;
		}
	},
	PLUSONE {

		@Override
		public Integer play(Integer coming) {
			return divide(coming + getNumber());
		}

		@Override
		public Integer getNumber() {
			return 1;
		}
	},
	INPUT {
		private Integer number;

		@Override
		public Integer play(Integer coming) {
			int min = 10;
			int max = 100;
			number = (int) Math.floor(Math.random() * (max - min + 1) + min);
			return coming + number;
		}

		@Override
		public Integer getNumber() {
			return number;
		}

	};

	public abstract Integer play(Integer number);

	public abstract Integer getNumber();

	private static Integer divide(Integer number) {
		return number / 3;
	}

	public static MoveEnum fromString(String text) {
		for (MoveEnum b : MoveEnum.values()) {
			if (b.name().equalsIgnoreCase(text)) {
				return b;
			}
		}
		return null;
	}
}
