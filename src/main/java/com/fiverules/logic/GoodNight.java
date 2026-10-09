package com.fiverules.logic;

import java.util.Locale;

/** Rule 6: say good night before sleeping. */
public final class GoodNight {
	/** "good night" must have been said at most 5 minutes before going to bed. */
	public static final long VALID_TICKS = 5 * 60 * 20;

	private GoodNight() {
	}

	/** Matches "good night", "Goodnight!", "GOOD   NIGHT everyone"... */
	public static boolean isGoodNight(String message) {
		String letters = message.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
		return letters.contains("goodnight");
	}

	public static boolean saidRecently(long saidAtTick, long nowTick) {
		return saidAtTick >= 0 && nowTick - saidAtTick <= VALID_TICKS;
	}
}
