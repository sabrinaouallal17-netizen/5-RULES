package com.fiverules.logic;

import java.util.Set;

/**
 * Rule 1: use the RIGHT tool for the RIGHT thing.
 */
public final class ToolRule {
	private ToolRule() {
	}

	/**
	 * @param blockTools the tool types the broken block is meant to be mined with (empty = no preferred tool)
	 * @param heldTool   the tool type held by the player, or {@code null} for bare hands / non-tool items
	 * @return {@code true} when the player broke a block with the wrong tool
	 */
	public static boolean isViolation(Set<ToolType> blockTools, ToolType heldTool) {
		if (heldTool == null || blockTools.isEmpty()) {
			return false;
		}
		return !blockTools.contains(heldTool);
	}
}
