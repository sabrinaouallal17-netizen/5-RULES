package com.fiverules.rules;

import com.fiverules.logic.TreeTracker;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/** Rule 4: felling 5 trees in a row empties your inventory (then 10 minutes of cooldown). */
public final class NatureRule {
	private static final Map<UUID, TreeTracker> TRACKERS = new HashMap<>();

	private NatureRule() {
	}

	public static void register() {
		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
			if (world.isClient()
					|| !(player instanceof ServerPlayerEntity serverPlayer)
					|| Punish.isExempt(player)
					|| !state.isIn(BlockTags.LOGS)
					|| !world.getBlockState(pos.down()).isIn(BlockTags.DIRT)) {
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
}
