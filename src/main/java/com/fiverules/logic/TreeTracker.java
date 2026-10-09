package com.fiverules.logic;

/**
 * Rule 4: do not break nature's beauty. Felling {@link #TREES_LIMIT} trees in a row empties the inventory,
 * then the rule sleeps for {@link #COOLDOWN_TICKS}. Times are in server ticks (20 ticks = 1 second).
 */
public final class TreeTracker {
	public static final int TREES_LIMIT = 5;
	/** Maximum gap between two trees for them to count as "in a row": 2 minutes. */
	public static final long STREAK_WINDOW_TICKS = 2 * 60 * 20;
	/** Cooldown after a punishment: 10 minutes. */
	public static final long COOLDOWN_TICKS = 10 * 60 * 20;

	private int streak;
	private long lastTreeTick = Long.MIN_VALUE;
	private long cooldownUntilTick = Long.MIN_VALUE;

	/**
	 * Records a felled tree.
	 *
	 * @return {@code true} when this tree triggers the punishment
	 */
	public boolean onTreeFelled(long nowTick) {
		if (nowTick < cooldownUntilTick) {
			return false;
		}
		if (streak > 0 && nowTick - lastTreeTick > STREAK_WINDOW_TICKS) {
			streak = 0;
		}
		streak++;
		lastTreeTick = nowTick;
		if (streak >= TREES_LIMIT) {
			streak = 0;
			cooldownUntilTick = nowTick + COOLDOWN_TICKS;
			return true;
		}
		return false;
	}

	public int streak() {
		return streak;
	}

	public boolean onCooldown(long nowTick) {
		return nowTick < cooldownUntilTick;
	}
}
