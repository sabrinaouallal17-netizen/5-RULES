package com.fiverules;

import com.fiverules.logic.DelayedActions;
import com.fiverules.network.CheaterPayload;
import com.fiverules.rules.AnimalRule;
import com.fiverules.rules.CheaterRule;
import com.fiverules.rules.NatureRule;
import com.fiverules.rules.ToolMisuseRule;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FiveRules implements ModInitializer {
	public static final String MOD_ID = "fiverules";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final DelayedActions SCHEDULER = new DelayedActions();

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.playC2S().register(CheaterPayload.ID, CheaterPayload.CODEC);

		ToolMisuseRule.register();
		AnimalRule.register();
		NatureRule.register();
		CheaterRule.register();

		ServerTickEvents.END_SERVER_TICK.register(server -> SCHEDULER.tick());

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			var player = handler.getPlayer();
			player.sendMessage(Text.literal("=== THE 5 RULES ===").formatted(Formatting.GOLD, Formatting.BOLD));
			player.sendMessage(Text.literal("1. Use the RIGHT tool for the RIGHT thing.").formatted(Formatting.YELLOW));
			player.sendMessage(Text.literal("2. Ask animals before killing them (sneak + right-click, empty hand).").formatted(Formatting.YELLOW));
			player.sendMessage(Text.literal("3. Do not waste food: only eat under half hunger.").formatted(Formatting.YELLOW));
			player.sendMessage(Text.literal("4. Do not break nature's beauty: 5 trees in a row is too many.").formatted(Formatting.YELLOW));
			player.sendMessage(Text.literal("5. Do not cheat: no F3!").formatted(Formatting.YELLOW));
		});

		LOGGER.info("5 RULES loaded. Good luck.");
	}
}
