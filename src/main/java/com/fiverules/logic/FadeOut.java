package com.fiverules.logic;

/** Rule 5: "Cheater" stays fully visible for a while, then fades out. */
public final class FadeOut {
	public static final long VISIBLE_MS = 3_000;
	public static final long FADE_MS = 1_000;

	private FadeOut() {
	}

	/** @return opacity between 0 (invisible) and 1 (fully visible) after {@code elapsedMs} */
	public static float alpha(long elapsedMs) {
		if (elapsedMs <= VISIBLE_MS) {
			return 1.0F;
		}
		long fading = elapsedMs - VISIBLE_MS;
		if (fading >= FADE_MS) {
			return 0.0F;
		}
		return 1.0F - (float) fading / FADE_MS;
	}
}
