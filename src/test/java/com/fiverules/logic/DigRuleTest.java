package com.fiverules.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DigRuleTest {
	@Test
	void blockUnderFeetIsDiggingDown() {
		assertTrue(DigRule.isUnderFeet(5, 64, -3, 5, 63, -3));
	}

	@Test
	void otherBlocksAreFine() {
		assertFalse(DigRule.isUnderFeet(5, 64, -3, 6, 63, -3));
		assertFalse(DigRule.isUnderFeet(5, 64, -3, 5, 62, -3));
		assertFalse(DigRule.isUnderFeet(5, 64, -3, 5, 64, -3));
	}
}
