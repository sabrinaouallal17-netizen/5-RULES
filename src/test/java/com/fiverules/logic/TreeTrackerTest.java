package com.fiverules.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TreeTrackerTest {
	@Test
	void fifthTreeInARowPunishes() {
		TreeTracker tracker = new TreeTracker();
		for (int i = 0; i < 4; i++) {
			assertFalse(tracker.onTreeFelled(i * 100L));
		}
		assertTrue(tracker.onTreeFelled(400));
		assertEquals(0, tracker.streak());
	}

	@Test
	void longPauseResetsStreak() {
		TreeTracker tracker = new TreeTracker();
		long t = 0;
		for (int i = 0; i < 4; i++) {
			tracker.onTreeFelled(t);
			t += 100;
		}
		t += TreeTracker.STREAK_WINDOW_TICKS + 1;
		assertFalse(tracker.onTreeFelled(t));
		assertEquals(1, tracker.streak());
	}

	@Test
	void gapExactlyAtWindowStillCounts() {
		TreeTracker tracker = new TreeTracker();
		long t = 0;
		for (int i = 0; i < 4; i++) {
			assertFalse(tracker.onTreeFelled(t));
			t += TreeTracker.STREAK_WINDOW_TICKS;
		}
		assertTrue(tracker.onTreeFelled(t));
	}

	@Test
	void cooldownIgnoresTreesForTenMinutes() {
		TreeTracker tracker = new TreeTracker();
		for (int i = 0; i < 5; i++) {
			tracker.onTreeFelled(i);
		}
		long punishedAt = 4;
		assertTrue(tracker.onCooldown(punishedAt + 1));
		for (int i = 0; i < 10; i++) {
			assertFalse(tracker.onTreeFelled(punishedAt + 1 + i));
		}
		assertEquals(0, tracker.streak());

		long afterCooldown = punishedAt + TreeTracker.COOLDOWN_TICKS;
		assertFalse(tracker.onCooldown(afterCooldown));
		for (int i = 0; i < 4; i++) {
			assertFalse(tracker.onTreeFelled(afterCooldown + i));
		}
		assertTrue(tracker.onTreeFelled(afterCooldown + 4));
	}
}
