package org.drugis.addis.entities.relativeeffect;


public class LogGaussian extends GaussianBase {
	public LogGaussian(double mu, double sigma) {
		super(mu, sigma);
	}

	public AxisType getAxisType() {
		return AxisType.LOGARITHMIC;
	}

	public double getQuantile(double p) {
		return Math.exp(calculateQuantile(p));
	}
}
