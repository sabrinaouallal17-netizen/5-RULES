package com.fiverules.logic;

/** Rules 8 and 14: works out whether a player is looking at the sun or the moon. */
public final class SunMath {
	/** Cosine of the largest angle (about 10 degrees) between the look direction and the sun that counts as staring. */
	public static final double STARE_COS = Math.cos(Math.toRadians(10));

	private SunMath() {
	}

	/**
	 * Sun direction for a sky angle (0 = noon, 0.25 = sunset in the west, 0.75 = sunrise in the east).
	 */
	public static double[] sunDirection(float skyAngle) {
		double angle = skyAngle * Math.PI * 2.0;
		return new double[] {-Math.sin(angle), Math.cos(angle), 0.0};
	}

	public static boolean isSunUp(float skyAngle) {
		return sunDirection(skyAngle)[1] > 0.0;
	}

	/** @param lookX, lookY, lookZ normalized look vector */
	public static boolean isLookingAtSun(double lookX, double lookY, double lookZ, float skyAngle) {
		double[] sun = sunDirection(skyAngle);
		if (sun[1] <= 0.0) {
			return false;
		}
		return lookX * sun[0] + lookY * sun[1] + lookZ * sun[2] >= STARE_COS;
	}

	/** The moon is always opposite the sun. */
	public static boolean isLookingAtMoon(double lookX, double lookY, double lookZ, float skyAngle) {
		double[] sun = sunDirection(skyAngle);
		if (sun[1] >= 0.0) {
			return false;
		}
		return -(lookX * sun[0] + lookY * sun[1] + lookZ * sun[2]) >= STARE_COS;
	}
}
