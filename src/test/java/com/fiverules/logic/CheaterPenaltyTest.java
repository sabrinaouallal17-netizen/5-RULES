package com.fiverules.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.Test;

class CheaterPenaltyTest {
	@Test
	void removesTheRolledAmount() {
		assertEquals(15, CheaterPenalty.apply(20, (min, max) -> 5));
	}

	@Test
	void neverDropsToZero() {
		assertEquals(1, CheaterPenalty.apply(3, (min, max) -> max));
	}

	@Test
	void alreadyStarvingIsUnchanged() {
		assertEquals(1, CheaterPenalty.apply(1, (min, max) -> max));
		assertEquals(0, CheaterPenalty.apply(0, (min, max) -> max));
	}

	@Test
	void lossStaysWithinBounds() {
		Random random = new Random(7);
		for (int i = 0; i < 1_000; i++) {
			int after = CheaterPenalty.apply(20, (min, max) -> min + random.nextInt(max - min + 1));
			int loss = 20 - after;
			assertTrue(loss >= CheaterPenalty.MIN_LOSS && loss <= CheaterPenalty.MAX_LOSS, "loss=" + loss);
		}
	}
}
