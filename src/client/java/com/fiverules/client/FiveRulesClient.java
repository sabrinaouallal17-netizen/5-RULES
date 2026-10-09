package com.fiverules.client;

import com.fiverules.network.CheaterPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class FiveRulesClient implements ClientModInitializer {
	private boolean debugWasOpen;

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			boolean open = client.player != null && client.inGameHud.getDebugHud().shouldShowDebugHud();
			if (open && !debugWasOpen && ClientPlayNetworking.canSend(CheaterPayload.ID)) {
				ClientPlayNetworking.send(CheaterPayload.INSTANCE);
			}
			debugWasOpen = open;
		});
	}
}
