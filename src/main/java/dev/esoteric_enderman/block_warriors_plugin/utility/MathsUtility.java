package dev.esoteric_enderman.block_warriors_plugin.utility;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

public final class MathsUtility {
	private static final Random RANDOM = new Random();

	@SafeVarargs
	public static <T> T chooseRandom(final T @NotNull ... values) {
		return values[RANDOM.nextInt(values.length)];
	}
}