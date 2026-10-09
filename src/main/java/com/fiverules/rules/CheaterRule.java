package com.fiverules.rules;

import com.fiverules.logic.CheaterPenalty;
import com.fiverules.network.CheaterPayload;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.server.network.ServerPlayerEntity;

/** Rule 5: opening F3 (reported by the client) eats a random part of your food bar. */
public final class CheaterRule {
	private static final int MIN_TICKS_BETWEEN_PENALTIES = 20;
	private static final Map<UUID, Integer> LAST_PENALTY = new HashMap<>();

	private CheaterRule() {
	}

	public static void register() {
		ServerPlayNetworking.registerGlobalReceiver(CheaterPayload.ID, (payload, context) -> punish(context.player()));
	}

	private static void punish(ServerPlayerEntity player) {
		if (Punish.isExempt(player)) {
			return;
		}
		int now = player.getServer().getTicks();
		Integer last = LAST_PENALTY.get(player.getUuid());
		if (last != null && now - last < MIN_TICKS_BETWEEN_PENALTIES) {
			return;
		}
		LAST_PENALTY.put(player.getUuid(), now);

		HungerManager hunger = player.getHungerManager();
		var random = player.getRandom();
		hunger.setFoodLevel(CheaterPenalty.apply(hunger.getFoodLevel(), random::nextBetween));
		Punish.announce(player, "RULE 5: Cheater! Your coordinates cost you some food.");
	}
}
