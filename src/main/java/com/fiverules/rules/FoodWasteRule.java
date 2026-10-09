package com.fiverules.rules;

import com.fiverules.logic.FoodRule;
import net.minecraft.block.Blocks;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/** Rule 3: eating with an almost full food bar drops an anvil on your head (up to 5 hearts of damage). */
public final class FoodWasteRule {
	private static final int ANVIL_HEIGHT = 12;

	private FoodWasteRule() {
	}

	/** Called from PlayerEntityMixin right before the player's hunger is refilled by food. */
	public static void onEat(ServerPlayerEntity player) {
		if (Punish.isExempt(player) || !FoodRule.isWasting(player.getHungerManager().getFoodLevel())) {
			return;
		}
		ServerWorld world = player.getServerWorld();
		Punish.announce(player, "RULE 3: You're not even hungry! Don't waste food.");

		BlockPos head = player.getBlockPos().up();
		BlockPos spawn = null;
		for (int i = 1; i <= ANVIL_HEIGHT && world.isAir(head.up(i)); i++) {
			spawn = head.up(i);
		}
		if (spawn == null) {
			player.damage(world.getDamageSources().fallingAnvil(player), FoodRule.MAX_ANVIL_DAMAGE);
			return;
		}
		FallingBlockEntity anvil = FallingBlockEntity.spawnFromBlock(world, spawn, Blocks.ANVIL.getDefaultState());
		anvil.setHurtEntities(2.0F, FoodRule.MAX_ANVIL_DAMAGE);
	}
}
