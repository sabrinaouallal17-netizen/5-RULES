package com.fiverules.logic;

/** Rule 12: stay hydrated. */
public final class Thirst {
	/** 20 minutes without a water bottle. */
	public static final long THIRSTY_AFTER_TICKS = 20 * 60 * 20;

	private Thirst() {
	}

	public static boolean isThirsty(long lastDrinkTick, long nowTick) {
		return nowTick - lastDrinkTick >= THIRSTY_AFTER_TICKS;
	}
}
