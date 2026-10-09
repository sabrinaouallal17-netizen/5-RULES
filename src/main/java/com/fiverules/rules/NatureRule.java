package com.fiverules.rules;

import com.fiverules.logic.TreeClusters;
import com.fiverules.logic.TreeTracker;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * Rule 4: felling 5 trees in a row empties your inventory (then 10 minutes of cooldown).
 * A tree counts as soon as any of its logs is broken, and only once.
 */
public final class NatureRule {
	private static final int MAX_SCANNED_LOGS = 128;
	private static final Map<UUID, TreeTracker> TRACKERS = new HashMap<>();
	private static final Map<UUID, TreeClusters> CLUSTERS = new HashMap<>();

	private NatureRule() {
	}

	public static void register() {
		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
			if (world.isClient()
					|| !(player instanceof ServerPlayerEntity serverPlayer)
					|| Punish.isExempt(player)
					|| !state.isIn(BlockTags.LOGS)) {
				return;
			}
			TreeClusters clusters = CLUSTERS.computeIfAbsent(player.getUuid(), id -> new TreeClusters());
			TreeClusters.Result result = clusters.onLogBroken(pos.getX(), pos.getY(), pos.getZ(), isNaturalTree(world, pos));
			if (result != TreeClusters.Result.NEW_TREE) {
				return;
			}
			TreeTracker tracker = TRACKERS.computeIfAbsent(player.getUuid(), id -> new TreeTracker());
			long now = serverPlayer.getServer().getTicks();
			if (tracker.onTreeFelled(now)) {
				serverPlayer.getInventory().clear();
				Punish.announce(serverPlayer, "RULE 4: Nature takes back everything you own!");
			} else if (!tracker.onCooldown(now)) {
				serverPlayer.sendMessage(Text.literal("Nature is watching... ("
						+ tracker.streak() + "/" + TreeTracker.TREES_LIMIT + ")").formatted(Formatting.DARK_GREEN), true);
			}
		});
	}

	/** A log is part of a natural tree when natural (non player-placed) leaves touch it or the logs connected to it. */
	private static boolean isNaturalTree(World world, BlockPos broken) {
		Set<BlockPos> visited = new HashSet<>();
		ArrayDeque<BlockPos> queue = new ArrayDeque<>();
		queue.add(broken);
		visited.add(broken);
		while (!queue.isEmpty() && visited.size() <= MAX_SCANNED_LOGS) {
			BlockPos log = queue.poll();
			for (Direction direction : Direction.values()) {
				if (isNaturalLeaves(world.getBlockState(log.offset(direction)))) {
					return true;
				}
			}
			for (BlockPos neighbor : BlockPos.iterate(log.add(-1, -1, -1), log.add(1, 1, 1))) {
				if (!visited.contains(neighbor) && world.getBlockState(neighbor).isIn(BlockTags.LOGS)) {
					BlockPos immutable = neighbor.toImmutable();
					visited.add(immutable);
					queue.add(immutable);
				}
			}
		}
		return false;
	}

	private static boolean isNaturalLeaves(BlockState state) {
		return state.isIn(BlockTags.LEAVES)
				&& state.contains(LeavesBlock.PERSISTENT)
				&& !state.get(LeavesBlock.PERSISTENT);
	}
}
