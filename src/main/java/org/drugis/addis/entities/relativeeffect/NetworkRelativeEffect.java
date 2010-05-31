package org.drugis.addis.entities.relativeeffect;

import org.drugis.addis.entities.Measurement;

public class NetworkRelativeEffect<T extends Measurement> extends AbstractRelativeEffect<T> implements RelativeEffect<T> {
	private Distribution d_distribution;
	private final boolean d_defined;
	
	public NetworkRelativeEffect(Distribution d) {
		d_distribution = d;
		d_defined = true;
	}
	
	public NetworkRelativeEffect() {
		d_defined = false;
	}

	static public NetworkRelativeEffect<? extends Measurement> buildOddsRatio(double mu, double sigma) {
		return new NetworkRelativeEffect<Measurement>(new LogGaussian(mu, sigma));
	}
	
	static public NetworkRelativeEffect<? extends Measurement> buildMeanDifference(double mu, double sigma) {
		return new NetworkRelativeEffect<Measurement>(new Gaussian(mu, sigma));
	}

	public Distribution getDistribution() {
		return d_distribution;
	}

	public String getName() {
		return "Network Meta-Analysis Relative Effect";
	}

	public boolean isDefined() {
		return d_defined;
	}
}