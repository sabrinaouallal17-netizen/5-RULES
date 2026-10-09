package com.fiverules.rules;

import com.fiverules.logic.ToolRule;
import com.fiverules.logic.ToolType;
import java.util.EnumSet;
import java.util.Set;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

/** Rule 1: breaking a block with the wrong tool makes you explode. */
public final class ToolMisuseRule {
	private ToolMisuseRule() {
	}

	public static void register() {
		PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
			if (world instanceof ServerWorld serverWorld
					&& player instanceof ServerPlayerEntity serverPlayer
					&& !Punish.isExempt(player)
					&& ToolRule.isViolation(preferredTools(state), heldTool(player.getMainHandStack()))) {
				Punish.announce(serverPlayer, "RULE 1: Wrong tool for the job! BOOM.");
				serverWorld.createExplosion(null, player.getX(), player.getY(), player.getZ(), 2.0F, World.ExplosionSourceType.NONE);
				Punish.kill(serverPlayer, serverWorld.getDamageSources().explosion(null, null));
			}
			return true;
		});
	}

	static Set<ToolType> preferredTools(BlockState state) {
		Set<ToolType> tools = EnumSet.noneOf(ToolType.class);
		if (state.isIn(BlockTags.PICKAXE_MINEABLE)) {
			tools.add(ToolType.PICKAXE);
		}
		if (state.isIn(BlockTags.AXE_MINEABLE)) {
			tools.add(ToolType.AXE);
		}
		if (state.isIn(BlockTags.SHOVEL_MINEABLE)) {
			tools.add(ToolType.SHOVEL);
		}
		if (state.isIn(BlockTags.HOE_MINEABLE)) {
			tools.add(ToolType.HOE);
		}
		return tools;
	}

	static ToolType heldTool(ItemStack stack) {
		if (stack.isIn(ItemTags.PICKAXES)) {
			return ToolType.PICKAXE;
		}
		if (stack.isIn(ItemTags.AXES)) {
			return ToolType.AXE;
		}
		if (stack.isIn(ItemTags.SHOVELS)) {
			return ToolType.SHOVEL;
		}
		if (stack.isIn(ItemTags.HOES)) {
			return ToolType.HOE;
		}
		return null;
	}
}
