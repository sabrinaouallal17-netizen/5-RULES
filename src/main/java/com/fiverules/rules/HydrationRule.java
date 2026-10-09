package com.fiverules.rules;

import com.fiverules.logic.Thirst;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/** Rule 12: 20 minutes without drinking a water bottle makes you slow until you drink one. */
public final class HydrationRule {
	private static final int CHECK_EVERY_TICKS = 20;
	private static final int SLOWNESS_TICKS = 60;
	private static final Map<UUID, Long> LAST_DRINK = new HashMap<>();
	private static final Set<UUID> THIRSTY = new HashSet<>();

	private HydrationRule() {
	}

	public static void register() {
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
				LAST_DRINK.put(handler.getPlayer().getUuid(), (long) server.getTicks()));
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			LAST_DRINK.remove(handler.getPlayer().getUuid());
			THIRSTY.remove(handler.getPlayer().getUuid());
		});
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			LAST_DRINK.put(newPlayer.getUuid(), (long) newPlayer.getServer().getTicks());
			THIRSTY.remove(newPlayer.getUuid());
		});

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			long now = server.getTicks();
			if (now % CHECK_EVERY_TICKS != 0) {
				return;
			}
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				long last = LAST_DRINK.computeIfAbsent(player.getUuid(), id -> now);
				if (Punish.isExempt(player) || !player.isAlive() || !Thirst.isThirsty(last, now)) {
					continue;
				}
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, SLOWNESS_TICKS, 0, false, false, true));
				if (THIRSTY.add(player.getUuid())) {
					Punish.announce(player, "RULE 12: You're dehydrated! Drink a water bottle.");
				}
			}
		});
	}

	/** Called from PotionItemMixin when a player finishes drinking a water bottle. */
	public static void onDrinkWater(ServerPlayerEntity player) {
		LAST_DRINK.put(player.getUuid(), (long) player.getServer().getTicks());
		if (THIRSTY.remove(player.getUuid())) {
			player.removeStatusEffect(StatusEffects.SLOWNESS);
			player.sendMessage(Text.literal("Ahh, refreshing.").formatted(Formatting.AQUA), true);
		}
	}
}
