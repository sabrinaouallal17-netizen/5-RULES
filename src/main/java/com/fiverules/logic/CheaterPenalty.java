package com.fiverules.logic;

import java.util.function.IntBinaryOperator;

/**
 * Rule 5: do not cheat. Opening F3 removes a random part of the food bar, never all of it.
 */
public final class CheaterPenalty {
	public static final int MIN_LOSS = 2;
	public static final int MAX_LOSS = 8;

	private CheaterPenalty() {
	}

	/**
	 * @param foodLevel   current food level (0-20)
	 * @param randomRange returns a random int in [min, max] (inclusive) for the given bounds
	 * @return the new food level, at least 1 (or unchanged when already at 1 or less)
	 */
	public static int apply(int foodLevel, IntBinaryOperator randomRange) {
		if (foodLevel <= 1) {
			return foodLevel;
		}
		int loss = randomRange.applyAsInt(MIN_LOSS, MAX_LOSS);
		return Math.max(1, foodLevel - loss);
	}
}
