package com.fiverules.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class DeadlinesTest {
	@Test
	void expiresAtDeadline() {
		Deadlines<String, String> deadlines = new Deadlines<>();
		deadlines.schedule("cow", "alice", 100);
		assertTrue(deadlines.popExpired(99).isEmpty());
		assertEquals(List.of("alice"), deadlines.popExpired(100));
		assertTrue(deadlines.popExpired(200).isEmpty());
	}

	@Test
	void cancelledNeverExpires() {
		Deadlines<String, String> deadlines = new Deadlines<>();
		deadlines.schedule("pig", "bob", 10);
		assertTrue(deadlines.cancel("pig"));
		assertFalse(deadlines.contains("pig"));
		assertTrue(deadlines.popExpired(50).isEmpty());
	}

	@Test
	void keysListsWaitingEntries() {
		Deadlines<String, String> deadlines = new Deadlines<>();
		deadlines.schedule("door", "alice", 10);
		assertEquals(List.of("door"), deadlines.keys());
		deadlines.cancel("door");
		assertTrue(deadlines.keys().isEmpty());
	}

	@Test
	void expiredEntriesKeepTheirKeys() {
		Deadlines<String, String> deadlines = new Deadlines<>();
		deadlines.schedule("door", "alice", 5);
		var expired = deadlines.popExpiredEntries(5);
		assertEquals(1, expired.size());
		assertEquals("door", expired.get(0).getKey());
		assertEquals("alice", expired.get(0).getValue());
	}
}
