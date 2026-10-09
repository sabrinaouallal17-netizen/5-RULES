package com.fiverules.logic;

/** Rule 7: don't dig straight down. */
public final class DigRule {
	/** Depth of the hole that opens under a player who digs straight down. */
	public static final int HOLE_DEPTH = 10;

	private DigRule() {
	}

	/** @return whether the broken block is the one the player stands on */
	public static boolean isUnderFeet(int playerX, int playerY, int playerZ, int blockX, int blockY, int blockZ) {
		return blockX == playerX && blockZ == playerZ && blockY == playerY - 1;
	}
}
