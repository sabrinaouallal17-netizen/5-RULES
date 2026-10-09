package com.fiverules.rules;

import com.fiverules.network.ReadBookPayload;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/** Rule 15: picking up a book forces you to read that book to the last page. */
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
		ServerPlayNetworking.send(player, new ReadBookPayload(pagesOf(stack)));
		Punish.announce(player, "RULE 15: You picked up a book. Now you read it. All of it.");
	}

	static List<Text> pagesOf(ItemStack stack) {
		List<Text> pages = new ArrayList<>();
		WrittenBookContentComponent written = stack.get(DataComponentTypes.WRITTEN_BOOK_CONTENT);
		if (written != null) {
			for (RawFilteredPair<Text> page : written.pages()) {
				pages.add(page.raw());
			}
		}
		WritableBookContentComponent writable = stack.get(DataComponentTypes.WRITABLE_BOOK_CONTENT);
		if (writable != null) {
			for (RawFilteredPair<String> page : writable.pages()) {
				pages.add(Text.literal(page.raw()));
			}
		}
		ItemEnchantmentsComponent enchantments = stack.get(DataComponentTypes.STORED_ENCHANTMENTS);
		if (enchantments != null && !enchantments.isEmpty()) {
			MutableText page = Text.empty().append(stack.getName().copy().formatted(Formatting.BOLD)).append("\n\n");
			for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : enchantments.getEnchantmentEntries()) {
				page.append(Enchantment.getName(entry.getKey(), entry.getIntValue())).append("\n");
			}
			pages.add(page);
		}
		if (pages.isEmpty()) {
			pages.add(Text.empty()
					.append(stack.getName().copy().formatted(Formatting.BOLD))
					.append("\n\nThis book is empty...\n\nBut you read it anyway. Every. Single. Page."));
		}
		return pages;
	}
}
