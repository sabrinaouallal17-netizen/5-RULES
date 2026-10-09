package com.fiverules.client;

import com.fiverules.logic.FadeOut;
import net.minecraft.util.Util;

/** Remembers when F3 was opened so "Cheater" can fade out. */
public final class CheaterOverlay {
	private static long openedAtMs = Util.getMeasuringTimeMs();

	private CheaterOverlay() {
	}

	public static void onDebugOpened() {
		openedAtMs = Util.getMeasuringTimeMs();
	}

	public static float alpha() {
		return FadeOut.alpha(Util.getMeasuringTimeMs() - openedAtMs);
	}
}
