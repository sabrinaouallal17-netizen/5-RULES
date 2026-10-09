package com.fiverules.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/** Values waiting for a deadline, keyed so they can be cancelled. Used for broken promises (rule 10) and open doors (rule 11). */
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

	/** A copy of the keys still waiting, safe to iterate while cancelling. */
	public List<K> keys() {
		return new ArrayList<>(entries.keySet());
	}

	/** Removes and returns every value whose deadline has passed. */
	public List<V> popExpired(long nowTick) {
		List<V> expired = new ArrayList<>();
		for (Map.Entry<K, V> entry : popExpiredEntries(nowTick)) {
			expired.add(entry.getValue());
		}
		return expired;
	}

	/** Removes and returns every key and value whose deadline has passed. */
	public List<Map.Entry<K, V>> popExpiredEntries(long nowTick) {
		List<Map.Entry<K, V>> expired = new ArrayList<>();
		Iterator<Map.Entry<K, Entry<V>>> it = entries.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<K, Entry<V>> entry = it.next();
			if (nowTick >= entry.getValue().deadlineTick) {
				it.remove();
				expired.add(Map.entry(entry.getKey(), entry.getValue().value));
			}
		}
		return expired;
	}

	private record Entry<V>(V value, long deadlineTick) {
	}
}
