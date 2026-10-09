package com.fiverules.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SunMathTest {
	@Test
	void lookingUpAtNoonIsStaring() {
		assertTrue(SunMath.isLookingAtSun(0, 1, 0, 0.0F));
	}

	@Test
	void lookingEastAtSunrise() {
		assertTrue(SunMath.isLookingAtSun(0.998, 0.063, 0, 0.76F));
		assertFalse(SunMath.isLookingAtSun(-1, 0, 0, 0.76F));
	}

	@Test
	void lookingAwayIsFine() {
		assertFalse(SunMath.isLookingAtSun(1, 0, 0, 0.0F));
		assertFalse(SunMath.isLookingAtSun(0, -1, 0, 0.0F));
	}

	@Test
	void noSunAtNight() {
		assertFalse(SunMath.isSunUp(0.5F));
		assertFalse(SunMath.isLookingAtSun(0, -1, 0, 0.5F));
		assertTrue(SunMath.isSunUp(0.1F));
	}
}
