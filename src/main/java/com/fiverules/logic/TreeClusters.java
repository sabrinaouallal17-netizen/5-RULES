package com.fiverules.logic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Rule 4: groups the logs a player breaks into trees, so a tree counts once
 * whichever of its logs is broken first.
 */
public final class TreeClusters {
	/** A log closer than this (on every axis) to a known tree belongs to that tree. Covers branches. */
	public static final int JOIN_DISTANCE = 2;
	private static final int MAX_TREES = 32;

	public enum Result {
		NEW_TREE,
		SAME_TREE,
		NOT_A_TREE
	}

	private final Deque<List<int[]>> trees = new ArrayDeque<>();

	/**
	 * @param looksLikeTree whether the broken log is part of a natural tree (e.g. natural leaves nearby)
	 */
	public Result onLogBroken(int x, int y, int z, boolean looksLikeTree) {
		for (List<int[]> tree : trees) {
			for (int[] log : tree) {
				if (Math.abs(log[0] - x) <= JOIN_DISTANCE
						&& Math.abs(log[1] - y) <= JOIN_DISTANCE
						&& Math.abs(log[2] - z) <= JOIN_DISTANCE) {
					tree.add(new int[] {x, y, z});
					return Result.SAME_TREE;
				}
			}
		}
		if (!looksLikeTree) {
			return Result.NOT_A_TREE;
		}
		List<int[]> tree = new ArrayList<>();
		tree.add(new int[] {x, y, z});
		trees.addFirst(tree);
		if (trees.size() > MAX_TREES) {
			trees.removeLast();
		}
		return Result.NEW_TREE;
	}
}
