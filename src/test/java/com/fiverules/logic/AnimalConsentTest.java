package com.fiverules.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class AnimalConsentTest {
	@Test
	void killingWithoutAskingIsForbidden() {
		AnimalConsent consent = new AnimalConsent(() -> true);
		assertFalse(consent.consumeKillAllowed(UUID.randomUUID()));
	}

	@Test
	void yesAllowsKill() {
		AnimalConsent consent = new AnimalConsent(() -> true);
		UUID cow = UUID.randomUUID();
		assertTrue(consent.ask(cow));
		assertTrue(consent.consumeKillAllowed(cow));
	}

	@Test
	void noForbidsKill() {
		AnimalConsent consent = new AnimalConsent(() -> false);
		UUID pig = UUID.randomUUID();
		assertFalse(consent.ask(pig));
		assertFalse(consent.consumeKillAllowed(pig));
	}

	@Test
	void answerIsRemembered() {
		AtomicInteger flips = new AtomicInteger();
		AnimalConsent consent = new AnimalConsent(() -> flips.incrementAndGet() == 1);
		UUID sheep = UUID.randomUUID();
		assertTrue(consent.ask(sheep));
		assertTrue(consent.ask(sheep));
		assertEquals(1, flips.get());
	}

	@Test
	void answerIsForgottenAfterDeath() {
		AnimalConsent consent = new AnimalConsent(() -> true);
		UUID chicken = UUID.randomUUID();
		consent.ask(chicken);
		assertTrue(consent.consumeKillAllowed(chicken));
		assertFalse(consent.consumeKillAllowed(chicken));
	}

	@Test
	void coinFlipIsRoughlyFiftyFifty() {
		Random random = new Random(42);
		AnimalConsent consent = new AnimalConsent(random::nextBoolean);
		int yes = 0;
		for (int i = 0; i < 10_000; i++) {
			if (consent.ask(UUID.randomUUID())) {
				yes++;
			}
		}
		assertTrue(yes > 4_700 && yes < 5_300, "yes=" + yes);
	}
}
