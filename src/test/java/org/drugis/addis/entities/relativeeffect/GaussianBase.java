package org.drugis.addis.entities.relativeeffect;

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

	protected double calcQuantile(double q) {
		return 0;
	}

	public double getSigma() {
		return d_sigma;
	}

	public double getMu() {
		return d_mu;
	}
}
