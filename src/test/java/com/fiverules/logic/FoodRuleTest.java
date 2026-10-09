package com.fiverules.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FoodRuleTest {
	@Test
	void hungryEnoughIsAllowed() {
		assertFalse(FoodRule.isWasting(0));
		assertFalse(FoodRule.isWasting(10));
		assertFalse(FoodRule.isWasting(15));
	}

	@Test
	void almostFullIsWasting() {
		assertTrue(FoodRule.isWasting(16));
		assertTrue(FoodRule.isWasting(20));
	}
}
