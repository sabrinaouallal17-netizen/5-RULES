package com.fiverules.rules;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/** Shared helpers for punishing rule breakers. */
public final class Punish {
	private Punish() {
	}

	/** Creative and spectator players are not subject to the rules. */
	public static boolean isExempt(PlayerEntity player) {
		return player.isCreative() || player.isSpectator();
	}

	/** Kills the player for sure: armor, totems and absorption do not save them. */
	public static void kill(ServerPlayerEntity player, DamageSource source) {
		if (!player.isAlive() || player.isRemoved()) {
			return;
		}
		player.damage(source, Float.MAX_VALUE);
		if (player.isAlive()) {
			player.kill();
		}
	}

	public static void announce(ServerPlayerEntity player, String message) {
		player.sendMessage(Text.literal(message).formatted(Formatting.RED, Formatting.BOLD));
	}
}
