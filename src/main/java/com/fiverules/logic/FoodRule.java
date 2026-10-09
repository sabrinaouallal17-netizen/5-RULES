package com.fiverules.logic;

/**
 * Rule 3: do not waste food. Eating is only allowed when the food bar is under half.
 */
public final class FoodRule {
	public static final int MAX_FOOD = 20;
	public static final int HALF_FOOD = MAX_FOOD / 2;

	private FoodRule() {
	}

	public static boolean isWasting(int foodLevel) {
		return foodLevel >= HALF_FOOD;
	}
}
