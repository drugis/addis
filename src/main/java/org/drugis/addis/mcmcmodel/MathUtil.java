package org.drugis.addis.mcmcmodel;

public class MathUtil {

	public static double logit(double p) {
		if (p > 0 && p < 1) {
			return Math.log(p / (1 - p));
		}
		throw new IllegalArgumentException("cannot take logit of " + p + ", out of bounds");
	}
	
	public static double ilogit(double x) {
		return 1 / (1 + Math.exp(-x));
	}
	
}
