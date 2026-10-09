package com.fiverules.logic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** A tiny tick-based scheduler: run an action after a number of ticks. */
public final class DelayedActions {
	private final List<Entry> entries = new ArrayList<>();

	public void schedule(int delayTicks, Runnable action) {
		entries.add(new Entry(Math.max(0, delayTicks), action));
	}

	/** Advances one tick and runs every action whose delay has elapsed. */
	public void tick() {
		List<Runnable> due = new ArrayList<>();
		Iterator<Entry> it = entries.iterator();
		while (it.hasNext()) {
			Entry entry = it.next();
			if (--entry.ticksLeft <= 0) {
				it.remove();
				due.add(entry.action);
			}
		}
		due.forEach(Runnable::run);
	}

	public int size() {
		return entries.size();
	}

	private static final class Entry {
		private int ticksLeft;
		private final Runnable action;

		private Entry(int ticksLeft, Runnable action) {
			this.ticksLeft = ticksLeft;
			this.action = action;
		}
	}
}
