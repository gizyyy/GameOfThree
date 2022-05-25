package de.gameofthree.application.model.enums;

public enum PlayerRoleEnum {
	PLAYER1("Number is being generated"), PLAYER2("Please wait");

	PlayerRoleEnum(String string) {
		this.setWelcomeText(string);
	}

	public String getWelcomeText() {
		return welcomeText;
	}

	public void setWelcomeText(String welcomeText) {
		this.welcomeText = welcomeText;
	}

	private String welcomeText;
}
