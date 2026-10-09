package com.fiverules.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BooleanSupplier;

/**
 * Rule 2: ask animals before killing them. Each animal answers once (50/50) and remembers its answer.
 */
public final class AnimalConsent {
	private final Map<UUID, Boolean> answers = new HashMap<>();
	private final BooleanSupplier coinFlip;

	public AnimalConsent(BooleanSupplier coinFlip) {
		this.coinFlip = coinFlip;
	}

	/** Asks the animal, returning its (possibly remembered) answer. */
	public boolean ask(UUID animal) {
		return answers.computeIfAbsent(animal, id -> coinFlip.getAsBoolean());
	}

	/** Killing is allowed only if the animal was asked and said yes. Forgets the animal afterwards. */
	public boolean consumeKillAllowed(UUID animal) {
		Boolean answer = answers.remove(animal);
		return Boolean.TRUE.equals(answer);
	}

	public void forget(UUID animal) {
		answers.remove(animal);
	}
}
