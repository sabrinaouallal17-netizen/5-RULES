package com.fiverules.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Rule 16: pets that are not fed for 20 minutes (while their owner plays) run away. */
public final class PetHunger {
	public static final int RUN_AWAY_TICKS = 20 * 60 * 20;
	public static final int WARNING_TICKS = 15 * 60 * 20;

	public enum Status {
		FINE,
		HUNGRY_WARNING,
		RUN_AWAY
	}

	private final Map<UUID, Integer> hungryTicks = new HashMap<>();

	public void feed(UUID pet) {
		hungryTicks.remove(pet);
	}

	public void forget(UUID pet) {
		hungryTicks.remove(pet);
	}

	/** Adds hungry time to a pet. Each status other than FINE is returned once, on the tick it is reached. */
	public Status addHunger(UUID pet, int ticks) {
		int before = hungryTicks.getOrDefault(pet, 0);
		int after = before + ticks;
		if (after >= RUN_AWAY_TICKS) {
			hungryTicks.remove(pet);
			return Status.RUN_AWAY;
		}
		hungryTicks.put(pet, after);
		return before < WARNING_TICKS && after >= WARNING_TICKS ? Status.HUNGRY_WARNING : Status.FINE;
	}
}
