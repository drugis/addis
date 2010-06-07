package org.drugis.addis.entities.relativeeffect;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;

public abstract class GaussianBase implements Distribution {
	private double d_mu;
	private double d_sigma;

	public GaussianBase(double mu, double sigma) {
		if (Double.isNaN(mu)) throw new IllegalArgumentException("mu may not be NaN");
		if (Double.isNaN(sigma)) throw new IllegalArgumentException("sigma may not be NaN");
		if (sigma < 0.0) throw new IllegalArgumentException("sigma must be >= 0.0");
		d_mu = mu;
		d_sigma = sigma;
	}

	protected double calculateQuantile(double p) {
		NormalDistribution dist = new NormalDistributionImpl(getMu(), getSigma());
		try {
			return dist.inverseCumulativeProbability(p);
		} catch (MathException e) {
			throw new RuntimeException(e);
		}
	}

	public double getSigma() {
		return d_sigma;
	}

	public double getMu() {
		return d_mu;
	}

	public GaussianBase plus(GaussianBase other) {
		if (!canEqual(other)) throw new IllegalArgumentException(
				"Cannot add together " + getClass().getSimpleName() +
				" and " + other.getClass().getSimpleName());
		return newInstance(getMu() + other.getMu(), 
				Math.sqrt(getSigma() * getSigma() + other.getSigma() * other.getSigma()));
	}

	protected abstract GaussianBase newInstance(double mu, double sigma);

	abstract protected boolean canEqual(GaussianBase other);
}
