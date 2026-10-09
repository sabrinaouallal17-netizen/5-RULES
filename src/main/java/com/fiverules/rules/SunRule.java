package com.fiverules.rules;

import com.fiverules.logic.ConditionTimer;
import com.fiverules.logic.SunMath;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/** Rule 8: staring at the sun for 3 seconds blinds you for 10 seconds. */
public final class SunRule {
	private static final int STARE_TICKS = 3 * 20;
	private static final int BLINDNESS_TICKS = 10 * 20;
	private static final Map<UUID, ConditionTimer> TIMERS = new HashMap<>();

	private SunRule() {
	}

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				ConditionTimer timer = TIMERS.computeIfAbsent(player.getUuid(), id -> new ConditionTimer(STARE_TICKS));
				if (timer.tick(isStaringAtSun(player))) {
					player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, BLINDNESS_TICKS));
					Punish.announce(player, "RULE 8: Don't stare at the sun!");
				}
			}
		});
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> TIMERS.remove(handler.getPlayer().getUuid()));
	}

	private static boolean isStaringAtSun(ServerPlayerEntity player) {
		ServerWorld world = player.getServerWorld();
		if (Punish.isExempt(player)
				|| world.getRegistryKey() != World.OVERWORLD
				|| world.isRaining()
				|| player.hasStatusEffect(StatusEffects.BLINDNESS)) {
			return false;
		}
		Vec3d look = player.getRotationVec(1.0F);
		return SunMath.isLookingAtSun(look.x, look.y, look.z, world.getSkyAngle(1.0F))
				&& world.isSkyVisible(BlockPos.ofFloored(player.getEyePos()));
	}
}
