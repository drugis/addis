package org.drugis.addis.entities.relativeeffect;

import org.drugis.mtc.util.Statistics;

public class LogitGaussian extends GaussianBase {
	public LogitGaussian(double mu, double sigma) {
		super(mu, sigma);
	}

	@Override
	protected boolean canEqual(GaussianBase other) {
		return (other instanceof LogitGaussian);
	}

	@Override
	protected GaussianBase newInstance(double mu, double sigma) {
		return new LogitGaussian(mu, sigma);
	}

	public AxisType getAxisType() {
		return AxisType.LINEAR;
	}

	public double getQuantile(double p) {
		return Statistics.ilogit(calculateQuantile(p));
	}
}
