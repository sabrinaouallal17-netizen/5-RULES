package com.fiverules.client.mixin;

import com.fiverules.client.CheaterOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Rule 5: the F3 screen shows nothing but "Cheater", which fades out after 3 seconds. */
@Mixin(DebugHud.class)
public abstract class DebugHudMixin {
	private static final String CHEATER = "Cheater";
	private static final float SCALE = 4.0F;
	/** Text alpha below this is treated as fully opaque by the text renderer. */
	private static final int MIN_ALPHA = 5;

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void fiverules$renderCheater(DrawContext context, CallbackInfo ci) {
		ci.cancel();
		int alpha = Math.round(CheaterOverlay.alpha() * 255.0F);
		if (alpha < MIN_ALPHA) {
			return;
		}
		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		float x = (context.getScaledWindowWidth() / SCALE - textRenderer.getWidth(CHEATER)) / 2.0F;
		context.getMatrices().push();
		context.getMatrices().scale(SCALE, SCALE, 1.0F);
		context.drawTextWithShadow(textRenderer, CHEATER, (int) x, 4, (alpha << 24) | 0xFF5555);
		context.getMatrices().pop();
	}
}
