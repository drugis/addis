package org.drugis.addis.entities.relativeeffect;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;

public class LogGaussian extends GaussianBase {
	public LogGaussian(double mu, double sigma) {
		super(mu, sigma);
	}

	public AxisType getAxisType() {
		return AxisType.LOGARITHMIC;
	}

	public double getQuantile(double p) {
		NormalDistribution dist = new NormalDistributionImpl(getMu(), getSigma());
		try {
			return Math.exp(dist.inverseCumulativeProbability(p));
		} catch (MathException e) {
			throw new RuntimeException(e);
		}
	}
}
