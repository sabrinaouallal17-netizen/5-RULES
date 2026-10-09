package com.fiverules.logic;

/**
 * Rule 3: do not waste food. Eating with an (almost) full food bar is punished.
 */
public final class FoodRule {
	public static final int MAX_FOOD = 20;
	/** Eating at this food level or above is wasting: 16/20 = 8 drumsticks out of 10. */
	public static final int WASTE_THRESHOLD = 16;
	/** Maximum damage the falling anvil can deal: 10 = 5 hearts. */
	public static final int MAX_ANVIL_DAMAGE = 10;

	private FoodRule() {
	}

	public static boolean isWasting(int foodLevel) {
		return foodLevel >= WASTE_THRESHOLD;
	}
}
