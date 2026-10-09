package com.fiverules.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ThirstTest {
	@Test
	void thirstyAfterTwentyMinutes() {
		assertFalse(Thirst.isThirsty(1_000, 1_000 + Thirst.THIRSTY_AFTER_TICKS - 1));
		assertTrue(Thirst.isThirsty(1_000, 1_000 + Thirst.THIRSTY_AFTER_TICKS));
	}
}
