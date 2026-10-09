package com.fiverules.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class DelayedActionsTest {
	@Test
	void runsAfterDelay() {
		DelayedActions actions = new DelayedActions();
		AtomicInteger runs = new AtomicInteger();
		actions.schedule(3, runs::incrementAndGet);
		actions.tick();
		actions.tick();
		assertEquals(0, runs.get());
		actions.tick();
		assertEquals(1, runs.get());
		actions.tick();
		assertEquals(1, runs.get());
		assertEquals(0, actions.size());
	}

	@Test
	void actionMayScheduleAnother() {
		DelayedActions actions = new DelayedActions();
		AtomicInteger runs = new AtomicInteger();
		actions.schedule(1, () -> actions.schedule(1, runs::incrementAndGet));
		actions.tick();
		actions.tick();
		assertEquals(1, runs.get());
	}
}
