package com.fiverules.rules;

import com.fiverules.logic.ConditionTimer;
import com.fiverules.logic.SunMath;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/** Rule 14: staring at the moon for 3 seconds summons a phantom right next to you. */
public final class MoonRule {
	private static final int STARE_TICKS = 3 * 20;
	private static final Map<UUID, ConditionTimer> TIMERS = new HashMap<>();

	private MoonRule() {
	}

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				ConditionTimer timer = TIMERS.computeIfAbsent(player.getUuid(), id -> new ConditionTimer(STARE_TICKS));
				if (timer.tick(isStaringAtMoon(player))) {
					summonPhantom(player);
					Punish.announce(player, "RULE 14: Don't look at the moon! Something looked back.");
				}
			}
		});
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> TIMERS.remove(handler.getPlayer().getUuid()));
	}

	private static boolean isStaringAtMoon(ServerPlayerEntity player) {
		ServerWorld world = player.getServerWorld();
		if (Punish.isExempt(player) || !player.isAlive() || world.getRegistryKey() != World.OVERWORLD || world.isRaining()) {
			return false;
		}
		Vec3d look = player.getRotationVec(1.0F);
		return SunMath.isLookingAtMoon(look.x, look.y, look.z, world.getSkyAngle(1.0F))
				&& world.isSkyVisible(BlockPos.ofFloored(player.getEyePos()));
	}

	private static void summonPhantom(ServerPlayerEntity player) {
		ServerWorld world = player.getServerWorld();
		PhantomEntity phantom = EntityType.PHANTOM.create(world);
		if (phantom == null) {
			return;
		}
		phantom.refreshPositionAndAngles(player.getX() + 1.5, player.getY() + 2.0, player.getZ(), 0.0F, 0.0F);
		phantom.setTarget(player);
		world.spawnEntity(phantom);
	}
}
