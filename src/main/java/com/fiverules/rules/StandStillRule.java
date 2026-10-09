package com.fiverules.rules;

import com.fiverules.logic.ConditionTimer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

/** Rule 9: standing still for 60 seconds summons an Angry Chicken that pecks you. */
public final class StandStillRule {
	/** Command tag that marks Angry Chickens, so rule 2 leaves them alone. */
	public static final String ANGRY_CHICKEN_TAG = "fiverules_angry_chicken";
	private static final int STILL_TICKS = 60 * 20;
	private static final int CHICKEN_LIFETIME_TICKS = 15 * 20;
	private static final double PECK_RANGE = 4.0;
	private static final float PECK_DAMAGE = 1.0F;
	private static final Map<UUID, ConditionTimer> TIMERS = new HashMap<>();
	private static final Map<UUID, Vec3d> LAST_POS = new HashMap<>();
	private static final List<AngryChicken> CHICKENS = new ArrayList<>();

	private StandStillRule() {
	}

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				Vec3d pos = player.getPos();
				Vec3d last = LAST_POS.put(player.getUuid(), pos);
				boolean still = last != null && last.squaredDistanceTo(pos) < 1.0E-4
						&& !Punish.isExempt(player) && !player.isSleeping() && player.isAlive();
				ConditionTimer timer = TIMERS.computeIfAbsent(player.getUuid(), id -> new ConditionTimer(STILL_TICKS));
				if (timer.tick(still)) {
					summonChicken(player);
				}
			}
			tickChickens(server);
		});
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			TIMERS.remove(handler.getPlayer().getUuid());
			LAST_POS.remove(handler.getPlayer().getUuid());
		});
	}

	private static void summonChicken(ServerPlayerEntity player) {
		ServerWorld world = player.getServerWorld();
		ChickenEntity chicken = EntityType.CHICKEN.create(world);
		if (chicken == null) {
			return;
		}
		chicken.refreshPositionAndAngles(player.getX() + 1.0, player.getY(), player.getZ(), 0.0F, 0.0F);
		chicken.setCustomName(Text.literal("Angry Chicken").formatted(Formatting.RED));
		chicken.addCommandTag(ANGRY_CHICKEN_TAG);
		world.spawnEntity(chicken);
		CHICKENS.add(new AngryChicken(chicken, player.getUuid()));
		Punish.announce(player, "RULE 9: Don't stand still! An Angry Chicken wants to talk.");
	}

	private static void tickChickens(MinecraftServer server) {
		Iterator<AngryChicken> it = CHICKENS.iterator();
		while (it.hasNext()) {
			AngryChicken angry = it.next();
			ChickenEntity chicken = angry.chicken;
			ServerPlayerEntity player = server.getPlayerManager().getPlayer(angry.player);
			if (--angry.ticksLeft <= 0 || !chicken.isAlive() || player == null) {
				if (chicken.isAlive()) {
					chicken.discard();
				}
				it.remove();
				continue;
			}
			if (player.getWorld() != chicken.getWorld()) {
				continue;
			}
			chicken.getNavigation().startMovingTo(player, 1.2);
			if (angry.ticksLeft % 20 == 0 && chicken.distanceTo(player) <= PECK_RANGE && !Punish.isExempt(player)) {
				chicken.swingHand(Hand.MAIN_HAND);
				player.damage(player.getServerWorld().getDamageSources().mobAttack(chicken), PECK_DAMAGE);
			}
		}
	}

	private static final class AngryChicken {
		private final ChickenEntity chicken;
		private final UUID player;
		private int ticksLeft = CHICKEN_LIFETIME_TICKS;

		private AngryChicken(ChickenEntity chicken, UUID player) {
			this.chicken = chicken;
			this.player = player;
		}
	}
}
