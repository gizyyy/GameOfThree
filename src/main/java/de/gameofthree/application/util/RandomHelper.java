package de.gameofthree.application.util;

import java.util.Random;

public class RandomHelper {

	public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
		int x = new Random().nextInt(clazz.getEnumConstants().length-1);
		return clazz.getEnumConstants()[x];
	}
}
