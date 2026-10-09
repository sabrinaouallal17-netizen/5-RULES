package com.fiverules.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/** Values waiting for a deadline, keyed so they can be cancelled. Rule 10 uses it for broken promises. */
public final class Deadlines<K, V> {
	private final Map<K, Entry<V>> entries = new HashMap<>();

	public void schedule(K key, V value, long deadlineTick) {
		entries.put(key, new Entry<>(value, deadlineTick));
	}

	public boolean cancel(K key) {
		return entries.remove(key) != null;
	}

	public boolean contains(K key) {
		return entries.containsKey(key);
	}

	/** Removes and returns every value whose deadline has passed. */
	public List<V> popExpired(long nowTick) {
		List<V> expired = new ArrayList<>();
		Iterator<Entry<V>> it = entries.values().iterator();
		while (it.hasNext()) {
			Entry<V> entry = it.next();
			if (nowTick >= entry.deadlineTick) {
				it.remove();
				expired.add(entry.value);
			}
		}
		return expired;
	}

	private record Entry<V>(V value, long deadlineTick) {
	}
}
