package com.fiverules.client;

import com.fiverules.network.CheaterPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class FiveRulesClient implements ClientModInitializer {
	private static final KeyBinding RULES_BOOK_KEY = new KeyBinding(
			"key.fiverules.rules_book", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, "category.fiverules");

	private boolean debugWasOpen;

	@Override
	public void onInitializeClient() {
		KeyBindingHelper.registerKeyBinding(RULES_BOOK_KEY);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (RULES_BOOK_KEY.wasPressed()) {
				if (client.currentScreen == null) {
					client.setScreen(new BookScreen(new BookScreen.Contents(RulesBook.pages())));
				}
			}

			boolean open = client.player != null && client.inGameHud.getDebugHud().shouldShowDebugHud();
			if (open && !debugWasOpen) {
				CheaterOverlay.onDebugOpened();
				if (ClientPlayNetworking.canSend(CheaterPayload.ID)) {
					ClientPlayNetworking.send(CheaterPayload.INSTANCE);
				}
			}
			debugWasOpen = open;
		});
	}
}
