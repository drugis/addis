package org.drugis.common;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.TDistribution;
import org.apache.commons.math.distribution.TDistributionImpl;

public class StudentTTable {
	/**
	 * Get the critical value for the Student's t distribution, for one-sided 0.025 probability of error.
	 * @param v Degrees of freedom.
	 * @return Critical value.
	 */
	public static double getT(int v) {
		TDistribution dist = new TDistributionImpl(v);
		try {
			return dist.inverseCumulativeProbability(0.975);
		} catch (MathException e) {
			throw new RuntimeException(e);
		}
	}
}
