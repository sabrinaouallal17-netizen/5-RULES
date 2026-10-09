package com.fiverules.rules;

import com.fiverules.FiveRules;
import com.fiverules.logic.FoodRule;
import net.minecraft.block.Blocks;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/** Rule 3: eating while at half hunger or more drops an anvil on your head. */
public final class FoodWasteRule {
	private static final int ANVIL_HEIGHT = 12;
	private static final int MAX_WAIT_TICKS = 60;

	private FoodWasteRule() {
	}

	/** Called from PlayerEntityMixin right before the player's hunger is refilled by food. */
	public static void onEat(ServerPlayerEntity player) {
		if (Punish.isExempt(player) || !FoodRule.isWasting(player.getHungerManager().getFoodLevel())) {
			return;
		}
		ServerWorld world = player.getServerWorld();
		Punish.announce(player, "RULE 3: You weren't even hungry! Don't waste food.");

		BlockPos head = player.getBlockPos().up();
		BlockPos spawn = null;
		for (int i = 1; i <= ANVIL_HEIGHT && world.isAir(head.up(i)); i++) {
			spawn = head.up(i);
		}
		if (spawn == null) {
			Punish.kill(player, world.getDamageSources().fallingAnvil(player));
			return;
		}
		FallingBlockEntity anvil = FallingBlockEntity.spawnFromBlock(world, spawn, Blocks.ANVIL.getDefaultState());
		anvil.setHurtEntities(2.0F, 40);
		waitForAnvil(player, anvil, MAX_WAIT_TICKS);
	}

	private static void waitForAnvil(ServerPlayerEntity player, FallingBlockEntity anvil, int ticksLeft) {
		if (!anvil.isAlive() || ticksLeft <= 0) {
			Punish.kill(player, player.getServerWorld().getDamageSources().fallingAnvil(anvil));
			return;
		}
		FiveRules.SCHEDULER.schedule(1, () -> waitForAnvil(player, anvil, ticksLeft - 1));
	}
}
