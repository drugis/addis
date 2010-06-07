package org.drugis.addis.entities.relativeeffect;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.TDistribution;
import org.apache.commons.math.distribution.TDistributionImpl;

public class TransformedStudentT implements Distribution {
	private double d_mu;
	private double d_sigma;
	private final int d_degreesOfFreedom;

	public TransformedStudentT(double mu, double sigma, int degreesOfFreedom) {
		if (Double.isNaN(mu)) throw new IllegalArgumentException("mu may not be NaN");
		if (Double.isNaN(sigma)) throw new IllegalArgumentException("sigma may not be NaN");
		if (sigma < 0.0) throw new IllegalArgumentException("sigma must be >= 0.0");
		if (degreesOfFreedom < 1) throw new IllegalArgumentException("degreesOfFreedom must be >= 1");
		d_mu = mu;
		d_sigma = sigma;
		d_degreesOfFreedom = degreesOfFreedom;
	}

	public AxisType getAxisType() {
		return AxisType.LINEAR;
	}

	public double getQuantile(double p) {
		TDistribution dist = new TDistributionImpl(getDegreesOfFreedom());
		try {
			return dist.inverseCumulativeProbability(p) * d_sigma + d_mu;
		} catch (MathException e) {
			throw new RuntimeException(e);
		}
	}

	public double getMu() {
		return d_mu;
	}

	public double getSigma() {
		return d_sigma;
	}

	public int getDegreesOfFreedom() {
		return d_degreesOfFreedom;
	}


}
