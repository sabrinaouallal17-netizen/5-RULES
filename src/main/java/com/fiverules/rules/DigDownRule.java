package com.fiverules.rules;

import com.fiverules.logic.DigRule;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/** Rule 7: digging straight down has 1 chance in 3 to open a 10-block hole under you. */
public final class DigDownRule {
	private DigDownRule() {
	}

	public static void register() {
		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
			if (!(world instanceof ServerWorld serverWorld)
					|| !(player instanceof ServerPlayerEntity serverPlayer)
					|| Punish.isExempt(player)) {
				return;
			}
			BlockPos feet = player.getBlockPos();
			if (!DigRule.isUnderFeet(feet.getX(), feet.getY(), feet.getZ(), pos.getX(), pos.getY(), pos.getZ())
					|| serverWorld.random.nextInt(3) != 0) {
				return;
			}
			Punish.announce(serverPlayer, "RULE 7: Never dig straight down!");
			for (int i = 1; i <= DigRule.HOLE_DEPTH; i++) {
				BlockPos below = pos.down(i);
				BlockState belowState = serverWorld.getBlockState(below);
				if (belowState.getHardness(serverWorld, below) < 0 || serverWorld.getBlockEntity(below) != null) {
					break;
				}
				serverWorld.removeBlock(below, false);
			}
		});
	}
}
