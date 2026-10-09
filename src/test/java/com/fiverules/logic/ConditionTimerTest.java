package com.fiverules.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ConditionTimerTest {
	@Test
	void firesAfterRequiredTicksInARow() {
		ConditionTimer timer = new ConditionTimer(3);
		assertFalse(timer.tick(true));
		assertFalse(timer.tick(true));
		assertTrue(timer.tick(true));
	}

	@Test
	void interruptionResets() {
		ConditionTimer timer = new ConditionTimer(3);
		timer.tick(true);
		timer.tick(true);
		assertFalse(timer.tick(false));
		assertFalse(timer.tick(true));
		assertFalse(timer.tick(true));
		assertTrue(timer.tick(true));
	}

	@Test
	void startsOverAfterFiring() {
		ConditionTimer timer = new ConditionTimer(2);
		timer.tick(true);
		assertTrue(timer.tick(true));
		assertFalse(timer.tick(true));
		assertTrue(timer.tick(true));
	}
}
