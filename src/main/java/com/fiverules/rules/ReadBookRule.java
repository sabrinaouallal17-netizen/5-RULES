package com.fiverules.rules;

import com.fiverules.network.ReadBookPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;

/** Rule 15: picking up a book forces you to read the rules book to the last page. */
public final class ReadBookRule {
	private ReadBookRule() {
	}

	/** Called from ItemEntityMixin when a player picks up (part of) an item stack. */
	public static void onPickup(ServerPlayerEntity player, ItemStack stack) {
		if (Punish.isExempt(player)
				|| !stack.isIn(ItemTags.BOOKSHELF_BOOKS)
				|| !ServerPlayNetworking.canSend(player, ReadBookPayload.ID)) {
			return;
		}
		ServerPlayNetworking.send(player, ReadBookPayload.INSTANCE);
		Punish.announce(player, "RULE 15: You picked up a book. Now you read it. All of it.");
	}
}
