package com.fiverules.rules;

import com.fiverules.logic.GoodNight;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/** Rule 6: say "good night" in the chat before sleeping, or wake up surrounded by zombies. */
public final class GoodNightRule {
	private static final int ZOMBIES = 3;
	private static final int[][] OFFSETS = {{2, 0}, {-2, 0}, {0, 2}, {0, -2}};
	private static final Map<UUID, Long> SAID_AT = new HashMap<>();
	private static final Set<UUID> RUDE_SLEEPERS = new HashSet<>();

	private GoodNightRule() {
	}

	public static void register() {
		ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
			if (GoodNight.isGoodNight(message.getSignedContent())) {
				SAID_AT.put(sender.getUuid(), (long) sender.getServer().getTicks());
			}
		});

		EntitySleepEvents.START_SLEEPING.register((entity, sleepingPos) -> {
			if (!(entity instanceof ServerPlayerEntity player) || Punish.isExempt(player)) {
				return;
			}
			Long saidAt = SAID_AT.remove(player.getUuid());
			if (!GoodNight.saidRecently(saidAt == null ? -1 : saidAt, player.getServer().getTicks())) {
				RUDE_SLEEPERS.add(player.getUuid());
			}
		});

		EntitySleepEvents.STOP_SLEEPING.register((entity, sleepingPos) -> {
			if (entity instanceof ServerPlayerEntity player && RUDE_SLEEPERS.remove(player.getUuid())) {
				Punish.announce(player, "RULE 6: You didn't say good night! The zombies came to tuck you in.");
				spawnZombies(player);
			}
		});
	}

	private static void spawnZombies(ServerPlayerEntity player) {
		ServerWorld world = player.getServerWorld();
		BlockPos center = player.getBlockPos();
		for (int i = 0; i < ZOMBIES; i++) {
			ZombieEntity zombie = EntityType.ZOMBIE.create(world);
			if (zombie == null) {
				continue;
			}
			BlockPos pos = center.add(OFFSETS[i][0], 0, OFFSETS[i][1]);
			if (!world.isAir(pos) || !world.isAir(pos.up())) {
				pos = center;
			}
			zombie.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, world.random.nextFloat() * 360.0F, 0.0F);
			// A helmet so the morning sun doesn't burn them right away.
			zombie.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
			zombie.setTarget(player);
			world.spawnEntity(zombie);
		}
	}
}
