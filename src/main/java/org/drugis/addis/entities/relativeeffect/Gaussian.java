package org.drugis.addis.entities.relativeeffect;


public class Gaussian extends GaussianBase {
	public Gaussian(double mu, double sigma) {
		super(mu, sigma);
	}

	public AxisType getAxisType() {
		return AxisType.LINEAR;
	}

	public double getQuantile(double p) {
		return calculateQuantile(p);
	}
}
