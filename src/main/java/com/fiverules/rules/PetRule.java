package com.fiverules.rules;

import com.fiverules.logic.PetHunger;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

/** Rule 16: tamed wolves, cats and parrots that are not fed for 20 minutes run away. */
public final class PetRule {
	private static final int CHECK_EVERY_TICKS = 100;
	private static final PetHunger HUNGER = new PetHunger();

	private PetRule() {
	}

	public static void register() {
		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (!world.isClient() && isPet(entity) && isPetFood((TameableEntity) entity, player.getStackInHand(hand))) {
				HUNGER.feed(entity.getUuid());
			}
			return ActionResult.PASS;
		});

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> HUNGER.forget(entity.getUuid()));

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTicks() % CHECK_EVERY_TICKS == 0) {
				tick(server);
			}
		});
	}

	private static boolean isPet(Entity entity) {
		return entity instanceof TameableEntity pet && pet.isTamed()
				&& (pet instanceof WolfEntity || pet instanceof CatEntity || pet instanceof ParrotEntity);
	}

	private static boolean isPetFood(TameableEntity pet, ItemStack stack) {
		return pet.isBreedingItem(stack) || (pet instanceof ParrotEntity && stack.isIn(ItemTags.PARROT_FOOD));
	}

	private static void tick(MinecraftServer server) {
		for (ServerWorld world : server.getWorlds()) {
			for (Entity entity : world.iterateEntities()) {
				if (!isPet(entity) || !(entity instanceof TameableEntity pet) || pet.getOwnerUuid() == null) {
					continue;
				}
				ServerPlayerEntity owner = server.getPlayerManager().getPlayer(pet.getOwnerUuid());
				// Pets only get hungry while their owner is playing.
				if (owner == null || Punish.isExempt(owner)) {
					continue;
				}
				switch (HUNGER.addHunger(pet.getUuid(), CHECK_EVERY_TICKS)) {
					case HUNGRY_WARNING -> owner.sendMessage(Text.literal("Your " + pet.getName().getString()
							+ " looks hungry... feed it!").formatted(Formatting.GOLD));
					case RUN_AWAY -> runAway(pet, owner);
					case FINE -> {
					}
				}
			}
		}
	}

	private static void runAway(TameableEntity pet, ServerPlayerEntity owner) {
		pet.setSitting(false);
		pet.setTamed(false, true);
		pet.setOwnerUuid(null);
		Vec3d away = pet.getPos().subtract(owner.getPos()).normalize().multiply(16.0);
		pet.getNavigation().startMovingTo(pet.getX() + away.x, pet.getY(), pet.getZ() + away.z, 1.4);
		Punish.announce(owner, "RULE 16: You forgot to feed your " + pet.getName().getString() + ". It ran away!");
	}
}
