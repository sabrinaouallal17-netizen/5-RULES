package com.fiverules.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

/** Rule 13: sleeping in armor breaks one random armor piece when you wake up. */
public final class ArmorSleepRule {
	private static final EquipmentSlot[] ARMOR_SLOTS = {
			EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
	private static final Set<UUID> SLEPT_IN_ARMOR = new HashSet<>();

	private ArmorSleepRule() {
	}

	public static void register() {
		EntitySleepEvents.START_SLEEPING.register((entity, sleepingPos) -> {
			if (entity instanceof ServerPlayerEntity player && !Punish.isExempt(player) && !wornArmor(player).isEmpty()) {
				SLEPT_IN_ARMOR.add(player.getUuid());
			}
		});

		EntitySleepEvents.STOP_SLEEPING.register((entity, sleepingPos) -> {
			if (!(entity instanceof ServerPlayerEntity player) || !SLEPT_IN_ARMOR.remove(player.getUuid())) {
				return;
			}
			List<EquipmentSlot> worn = wornArmor(player);
			if (worn.isEmpty()) {
				return;
			}
			EquipmentSlot slot = worn.get(player.getRandom().nextInt(worn.size()));
			String name = player.getEquippedStack(slot).getName().getString();
			player.equipStack(slot, ItemStack.EMPTY);
			player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1.0F, 1.0F);
			Punish.announce(player, "RULE 13: Who sleeps in armor? Your " + name + " broke.");
		});
	}

	private static List<EquipmentSlot> wornArmor(ServerPlayerEntity player) {
		List<EquipmentSlot> worn = new ArrayList<>();
		for (EquipmentSlot slot : ARMOR_SLOTS) {
			if (!player.getEquippedStack(slot).isEmpty()) {
				worn.add(slot);
			}
		}
		return worn;
	}
}
