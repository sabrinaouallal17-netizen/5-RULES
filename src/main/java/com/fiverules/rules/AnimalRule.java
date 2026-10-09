package com.fiverules.rules;

import com.fiverules.logic.AnimalConsent;
import com.fiverules.logic.Deadlines;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

/**
 * Rule 2: ask animals (sneak + right-click with an empty hand) before killing them.
 * Rule 10: an animal that said yes and is still alive a minute later feels lied to and attacks you.
 */
public final class AnimalRule {
	private static final AnimalConsent CONSENT = new AnimalConsent(() -> ThreadLocalRandom.current().nextBoolean());
	private static final long PROMISE_TICKS = 60 * 20;
	private static final double GIVE_UP_DISTANCE = 48.0;
	private static final double HIT_RANGE = 2.0;
	private static final float HIT_DAMAGE = 1.0F;
	private static final Deadlines<UUID, Promise> PROMISES = new Deadlines<>();
	private static final Map<UUID, Promise> OFFENDED = new HashMap<>();

	private AnimalRule() {
	}

	private record Promise(ServerWorld world, UUID animal, UUID player) {
	}

	public static void register() {
		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (hand != Hand.MAIN_HAND
					|| !player.isSneaking()
					|| !player.getMainHandStack().isEmpty()
					|| player.isSpectator()
					|| !isProtectedAnimal(entity)) {
				return ActionResult.PASS;
			}
			if (world instanceof ServerWorld serverWorld) {
				boolean firstAnswer = !CONSENT.hasAnswered(entity.getUuid());
				boolean yes = CONSENT.ask(entity.getUuid());
				String name = entity.getName().getString();
				Text answer = yes
						? Text.literal("[" + name + "] Yes... you may kill me.").formatted(Formatting.GREEN)
						: Text.literal("[" + name + "] NO! Don't you dare.").formatted(Formatting.RED);
				player.sendMessage(answer, false);
				serverWorld.spawnParticles(yes ? ParticleTypes.HAPPY_VILLAGER : ParticleTypes.ANGRY_VILLAGER,
						entity.getX(), entity.getEyeY() + 0.5, entity.getZ(), 5, 0.3, 0.3, 0.3, 0.0);
				if (yes && firstAnswer && !Punish.isExempt(player)) {
					PROMISES.schedule(entity.getUuid(), new Promise(serverWorld, entity.getUuid(), player.getUuid()),
							serverWorld.getServer().getTicks() + PROMISE_TICKS);
				}
			}
			return ActionResult.SUCCESS;
		});

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
			if (!isProtectedAnimal(entity)) {
				return;
			}
			PROMISES.cancel(entity.getUuid());
			OFFENDED.remove(entity.getUuid());
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

		ServerTickEvents.END_SERVER_TICK.register(AnimalRule::tickLiedTo);
	}

	/** Angry Chickens from rule 9 are fair game. */
	private static boolean isProtectedAnimal(Entity entity) {
		return entity instanceof AnimalEntity && !entity.getCommandTags().contains(StandStillRule.ANGRY_CHICKEN_TAG);
	}

	private static void tickLiedTo(MinecraftServer server) {
		int now = server.getTicks();
		for (Promise promise : PROMISES.popExpired(now)) {
			ServerPlayerEntity player = server.getPlayerManager().getPlayer(promise.player());
			Entity animal = promise.world().getEntity(promise.animal());
			if (player != null && animal != null && animal.isAlive()) {
				OFFENDED.put(promise.animal(), promise);
				Punish.announce(player, "RULE 10: You lied to " + animal.getName().getString()
						+ "! It said yes and you didn't even kill it. Now it's offended.");
			}
		}

		Iterator<Promise> it = OFFENDED.values().iterator();
		while (it.hasNext()) {
			Promise promise = it.next();
			ServerPlayerEntity player = server.getPlayerManager().getPlayer(promise.player());
			if (!(promise.world().getEntity(promise.animal()) instanceof AnimalEntity animal)
					|| !animal.isAlive()
					|| player == null
					|| player.getServerWorld() != promise.world()
					|| animal.distanceTo(player) > GIVE_UP_DISTANCE) {
				it.remove();
				continue;
			}
			if (now % 10 == 0) {
				animal.getNavigation().startMovingTo(player, 1.3);
			}
			if (now % 20 == 0 && animal.distanceTo(player) <= HIT_RANGE && !Punish.isExempt(player)) {
				animal.swingHand(Hand.MAIN_HAND);
				player.damage(promise.world().getDamageSources().mobAttack(animal), HIT_DAMAGE);
			}
		}
	}
}
