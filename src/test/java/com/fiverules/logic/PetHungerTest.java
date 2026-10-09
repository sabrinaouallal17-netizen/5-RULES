package com.fiverules.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class PetHungerTest {
	@Test
	void warnsOnceThenRunsAway() {
		PetHunger hunger = new PetHunger();
		UUID dog = UUID.randomUUID();
		assertEquals(PetHunger.Status.FINE, hunger.addHunger(dog, PetHunger.WARNING_TICKS - 100));
		assertEquals(PetHunger.Status.HUNGRY_WARNING, hunger.addHunger(dog, 100));
		assertEquals(PetHunger.Status.FINE, hunger.addHunger(dog, 100));
		assertEquals(PetHunger.Status.RUN_AWAY, hunger.addHunger(dog, PetHunger.RUN_AWAY_TICKS));
	}

	@Test
	void feedingResetsHunger() {
		PetHunger hunger = new PetHunger();
		UUID cat = UUID.randomUUID();
		hunger.addHunger(cat, PetHunger.RUN_AWAY_TICKS - 1);
		hunger.feed(cat);
		assertEquals(PetHunger.Status.FINE, hunger.addHunger(cat, PetHunger.WARNING_TICKS - 1));
	}

	@Test
	void petsAreIndependent() {
		PetHunger hunger = new PetHunger();
		UUID a = UUID.randomUUID();
		UUID b = UUID.randomUUID();
		hunger.addHunger(a, PetHunger.RUN_AWAY_TICKS - 1);
		assertEquals(PetHunger.Status.FINE, hunger.addHunger(b, 1));
	}
}
