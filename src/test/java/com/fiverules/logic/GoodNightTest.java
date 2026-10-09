package com.fiverules.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GoodNightTest {
	@Test
	void recognisesGoodNight() {
		assertTrue(GoodNight.isGoodNight("good night"));
		assertTrue(GoodNight.isGoodNight("Goodnight!"));
		assertTrue(GoodNight.isGoodNight("GOOD   NIGHT everyone"));
		assertTrue(GoodNight.isGoodNight("ok good-night"));
	}

	@Test
	void ignoresOtherMessages() {
		assertFalse(GoodNight.isGoodNight("good morning"));
		assertFalse(GoodNight.isGoodNight("night"));
		assertFalse(GoodNight.isGoodNight(""));
	}

	@Test
	void onlyRecentGoodNightCounts() {
		assertFalse(GoodNight.saidRecently(-1, 100));
		assertTrue(GoodNight.saidRecently(100, 100 + GoodNight.VALID_TICKS));
		assertFalse(GoodNight.saidRecently(100, 101 + GoodNight.VALID_TICKS));
	}
}
