package com.fiverules.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FoodRuleTest {
	@Test
	void underHalfIsAllowed() {
		assertFalse(FoodRule.isWasting(0));
		assertFalse(FoodRule.isWasting(9));
	}

	@Test
	void halfOrMoreIsWasting() {
		assertTrue(FoodRule.isWasting(10));
		assertTrue(FoodRule.isWasting(20));
	}
}
