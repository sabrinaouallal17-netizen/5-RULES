package com.fiverules;

import com.fiverules.network.CheaterPayload;
import com.fiverules.network.ReadBookPayload;
import com.fiverules.rules.AnimalRule;
import com.fiverules.rules.ArmorSleepRule;
import com.fiverules.rules.CheaterRule;
import com.fiverules.rules.DigDownRule;
import com.fiverules.rules.DoorRule;
import com.fiverules.rules.GoodNightRule;
import com.fiverules.rules.HydrationRule;
import com.fiverules.rules.MoonRule;
import com.fiverules.rules.NatureRule;
import com.fiverules.rules.PetRule;
import com.fiverules.rules.StandStillRule;
import com.fiverules.rules.SunRule;
import com.fiverules.rules.ToolMisuseRule;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FiveRules implements ModInitializer {
	public static final String MOD_ID = "fiverules";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.playC2S().register(CheaterPayload.ID, CheaterPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(ReadBookPayload.ID, ReadBookPayload.CODEC);

		ToolMisuseRule.register();
		AnimalRule.register();
		NatureRule.register();
		CheaterRule.register();
		GoodNightRule.register();
		DigDownRule.register();
		SunRule.register();
		StandStillRule.register();
		DoorRule.register();
		HydrationRule.register();
		ArmorSleepRule.register();
		MoonRule.register();
		PetRule.register();

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			var player = handler.getPlayer();
			player.sendMessage(Text.literal("=== THE RULES ===").formatted(Formatting.GOLD, Formatting.BOLD));
			player.sendMessage(Text.literal("1. Use the RIGHT tool for the RIGHT thing.").formatted(Formatting.YELLOW));
			player.sendMessage(Text.literal("2. Ask animals before killing them (sneak + right-click, empty hand).").formatted(Formatting.YELLOW));
			player.sendMessage(Text.literal("3. Do not waste food: no eating with an almost full food bar.").formatted(Formatting.YELLOW));
			player.sendMessage(Text.literal("4. Do not break nature's beauty: 5 trees in a row is too many.").formatted(Formatting.YELLOW));
			player.sendMessage(Text.literal("5. Do not cheat: no F3!").formatted(Formatting.YELLOW));
			player.sendMessage(Text.literal("6. Say good night before sleeping.").formatted(Formatting.YELLOW));
			player.sendMessage(Text.literal("7. Don't dig straight down.").formatted(Formatting.YELLOW));
			player.sendMessage(Text.literal("8. Don't stare at the sun.").formatted(Formatting.YELLOW));
			player.sendMessage(Text.literal("9. Don't stand still.").formatted(Formatting.YELLOW));
			player.sendMessage(Text.literal("10. Don't lie to animals: if one says yes, keep your word.").formatted(Formatting.YELLOW));
			player.sendMessage(Text.literal("11. Close the door behind you.").formatted(Formatting.YELLOW));
			player.sendMessage(Text.literal("12. Stay hydrated: drink a water bottle every 20 minutes.").formatted(Formatting.YELLOW));
			player.sendMessage(Text.literal("13. Don't sleep in armor.").formatted(Formatting.YELLOW));
			player.sendMessage(Text.literal("14. Don't look at the moon.").formatted(Formatting.YELLOW));
			player.sendMessage(Text.literal("15. Read every book you pick up.").formatted(Formatting.YELLOW));
			player.sendMessage(Text.literal("16. Feed your pets.").formatted(Formatting.YELLOW));
			player.sendMessage(Text.literal("Press ").append(Text.keybind("key.fiverules.rules_book"))
					.append(" to read the rules book.").formatted(Formatting.GRAY, Formatting.ITALIC));
		});

		LOGGER.info("5 RULES loaded. Good luck.");
	}
}
