package com.fiverules.rules;

import com.fiverules.logic.AnimalConsent;
import java.util.concurrent.ThreadLocalRandom;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

/** Rule 2: ask animals (sneak + right-click with an empty hand) before killing them. */
public final class AnimalRule {
	private static final AnimalConsent CONSENT = new AnimalConsent(() -> ThreadLocalRandom.current().nextBoolean());

	private AnimalRule() {
	}

	public static void register() {
		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (hand != Hand.MAIN_HAND
					|| !player.isSneaking()
					|| !player.getMainHandStack().isEmpty()
					|| player.isSpectator()
					|| !(entity instanceof AnimalEntity)) {
				return ActionResult.PASS;
			}
			if (world instanceof ServerWorld serverWorld) {
				boolean yes = CONSENT.ask(entity.getUuid());
				String name = entity.getName().getString();
				Text answer = yes
						? Text.literal("[" + name + "] Yes... you may kill me.").formatted(Formatting.GREEN)
						: Text.literal("[" + name + "] NO! Don't you dare.").formatted(Formatting.RED);
				player.sendMessage(answer, false);
				serverWorld.spawnParticles(yes ? ParticleTypes.HAPPY_VILLAGER : ParticleTypes.ANGRY_VILLAGER,
						entity.getX(), entity.getEyeY() + 0.5, entity.getZ(), 5, 0.3, 0.3, 0.3, 0.0);
			}
			return ActionResult.SUCCESS;
		});

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
			if (!(entity instanceof AnimalEntity)) {
				return;
			}
			boolean allowed = CONSENT.consumeKillAllowed(entity.getUuid());
			if (allowed
					|| !(damageSource.getAttacker() instanceof ServerPlayerEntity player)
					|| Punish.isExempt(player)) {
				return;
			}
			ServerWorld world = player.getServerWorld();
			Punish.announce(player, "RULE 2: You didn't have " + entity.getName().getString() + "'s consent!");
			LightningEntity bolt = EntityType.LIGHTNING_BOLT.create(world);
			if (bolt != null) {
				bolt.refreshPositionAfterTeleport(player.getPos());
				bolt.setCosmetic(true);
				world.spawnEntity(bolt);
			}
			Punish.kill(player, world.getDamageSources().lightningBolt());
		});
	}
}
