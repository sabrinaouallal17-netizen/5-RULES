package com.fiverules.logic;

/** Fires once a condition has held for a number of ticks in a row, then starts counting again. */
public final class ConditionTimer {
	private final int requiredTicks;
	private int ticks;

	public ConditionTimer(int requiredTicks) {
		this.requiredTicks = requiredTicks;
	}

	/** @return {@code true} on the tick the condition has held for {@code requiredTicks} in a row */
	public boolean tick(boolean condition) {
		if (!condition) {
			ticks = 0;
			return false;
		}
		if (++ticks >= requiredTicks) {
			ticks = 0;
			return true;
		}
		return false;
	}

	public void reset() {
		ticks = 0;
	}
}
