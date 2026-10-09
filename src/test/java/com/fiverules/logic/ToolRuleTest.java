package com.fiverules.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ToolRuleTest {
	@Test
	void pickaxeOnLogIsViolation() {
		assertTrue(ToolRule.isViolation(EnumSet.of(ToolType.AXE), ToolType.PICKAXE));
	}

	@Test
	void axeOnStoneIsViolation() {
		assertTrue(ToolRule.isViolation(EnumSet.of(ToolType.PICKAXE), ToolType.AXE));
	}

	@Test
	void rightToolIsFine() {
		assertFalse(ToolRule.isViolation(EnumSet.of(ToolType.AXE), ToolType.AXE));
		assertFalse(ToolRule.isViolation(EnumSet.of(ToolType.PICKAXE), ToolType.PICKAXE));
	}

	@Test
	void anyAcceptedToolIsFine() {
		assertFalse(ToolRule.isViolation(EnumSet.of(ToolType.AXE, ToolType.HOE), ToolType.HOE));
	}

	@Test
	void bareHandsAreAllowed() {
		assertFalse(ToolRule.isViolation(EnumSet.of(ToolType.PICKAXE), null));
	}

	@Test
	void blocksWithoutPreferredToolAreFree() {
		assertFalse(ToolRule.isViolation(Set.of(), ToolType.PICKAXE));
	}
}
