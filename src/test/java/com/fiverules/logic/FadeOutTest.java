package com.fiverules.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FadeOutTest {
	@Test
	void fullyVisibleForThreeSeconds() {
		assertEquals(1.0F, FadeOut.alpha(0));
		assertEquals(1.0F, FadeOut.alpha(3_000));
	}

	@Test
	void fadesDuringTheNextSecond() {
		assertEquals(0.5F, FadeOut.alpha(3_500), 0.001F);
	}

	@Test
	void invisibleAfterwards() {
		assertEquals(0.0F, FadeOut.alpha(4_000));
		assertEquals(0.0F, FadeOut.alpha(60_000));
	}
}
