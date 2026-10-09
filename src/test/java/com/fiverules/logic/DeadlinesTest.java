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
}
