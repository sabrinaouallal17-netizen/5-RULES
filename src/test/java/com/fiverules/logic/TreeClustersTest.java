package com.fiverules.logic;

import static com.fiverules.logic.TreeClusters.Result.NEW_TREE;
import static com.fiverules.logic.TreeClusters.Result.NOT_A_TREE;
import static com.fiverules.logic.TreeClusters.Result.SAME_TREE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TreeClustersTest {
	@Test
	void topLogFirstCountsTheTreeOnce() {
		TreeClusters clusters = new TreeClusters();
		assertEquals(NEW_TREE, clusters.onLogBroken(0, 68, 0, true));
		for (int y = 67; y >= 64; y--) {
			assertEquals(SAME_TREE, clusters.onLogBroken(0, y, 0, false));
		}
	}

	@Test
	void bottomLogFirstCountsTheTreeOnce() {
		TreeClusters clusters = new TreeClusters();
		assertEquals(NEW_TREE, clusters.onLogBroken(0, 64, 0, true));
		for (int y = 65; y <= 68; y++) {
			assertEquals(SAME_TREE, clusters.onLogBroken(0, y, 0, true));
		}
	}

	@Test
	void branchesBelongToTheSameTree() {
		TreeClusters clusters = new TreeClusters();
		clusters.onLogBroken(0, 70, 0, true);
		assertEquals(SAME_TREE, clusters.onLogBroken(2, 71, 1, true));
		assertEquals(SAME_TREE, clusters.onLogBroken(4, 72, 2, true));
	}

	@Test
	void separateTreesCountSeparately() {
		TreeClusters clusters = new TreeClusters();
		assertEquals(NEW_TREE, clusters.onLogBroken(0, 64, 0, true));
		assertEquals(NEW_TREE, clusters.onLogBroken(5, 64, 0, true));
		assertEquals(NEW_TREE, clusters.onLogBroken(0, 64, 5, true));
	}

	@Test
	void placedLogsAreNotTrees() {
		TreeClusters clusters = new TreeClusters();
		assertEquals(NOT_A_TREE, clusters.onLogBroken(10, 64, 10, false));
		assertEquals(NOT_A_TREE, clusters.onLogBroken(10, 65, 10, false));
	}
}
