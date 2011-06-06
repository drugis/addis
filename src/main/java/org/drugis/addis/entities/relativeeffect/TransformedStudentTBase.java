package org.drugis.addis.entities.relativeeffect;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.TDistribution;
import org.apache.commons.math.distribution.TDistributionImpl;
import org.drugis.common.beans.AbstractObservable;

public abstract class TransformedStudentTBase extends AbstractObservable implements	Distribution {

	protected final double d_mu;
	protected final double d_sigma;
	protected final int d_degreesOfFreedom;
	protected final TDistribution d_dist;

	public TransformedStudentTBase(double mu, double sigma, int degreesOfFreedom) {
		if (Double.isNaN(mu)) throw new IllegalArgumentException("mu may not be NaN");
		if (Double.isNaN(sigma)) throw new IllegalArgumentException("sigma may not be NaN");
		if (sigma < 0.0) throw new IllegalArgumentException("sigma must be >= 0.0");
		if (degreesOfFreedom < 1) throw new IllegalArgumentException("degreesOfFreedom must be >= 1");
		d_mu = mu;
		d_sigma = sigma;
		d_degreesOfFreedom = degreesOfFreedom;
		d_dist = new TDistributionImpl(getDegreesOfFreedom());
	}

	protected double calculateQuantile(double p) {
		try {
			return d_dist.inverseCumulativeProbability(p) * d_sigma + d_mu;
		} catch (MathException e) {
			throw new RuntimeException(e);
		}
	}

	protected double calculateCumulativeProbability(double x) {
		try {
			return d_dist.cumulativeProbability((x - d_mu) / d_sigma);
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


	@Override
	public boolean equals(Object o) {
		if(o instanceof TransformedStudentTBase) {
			TransformedStudentTBase other = (TransformedStudentTBase) o;
			return other.d_mu == d_mu && other.d_sigma == d_sigma && other.d_degreesOfFreedom == d_degreesOfFreedom && other.getClass().equals(getClass());
		}
		return false;
	}
}