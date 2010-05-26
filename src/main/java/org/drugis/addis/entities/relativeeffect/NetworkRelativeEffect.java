package org.drugis.addis.entities.relativeeffect;

import java.util.Set;

import org.drugis.addis.entities.AbstractEntity;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Measurement;

public class NetworkRelativeEffect<T extends Measurement> extends AbstractEntity implements RelativeEffect<T> {
	private Distribution d_distribution;

	public NetworkRelativeEffect(Distribution d) {
		d_distribution = d;
	}

	static public NetworkRelativeEffect<?> buildOddsRatio(double mu, double sigma) {
		return null;
	}
	
	static public NetworkRelativeEffect<?> buildMeanDifference(double mu, double sigma) {
		return null;
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		return null;
	}

	public AxisType getAxisType() {
		return getDistribution().getAxisType();
	}

	public T getBaseline() {
		return null;
	}

	public T getSubject() {
		return null;
	}

	public ConfidenceInterval getConfidenceInterval() {
		return new ConfidenceInterval(getDistribution().getQuantile(0.5), 
				getDistribution().getQuantile(0.025), getDistribution().getQuantile(0.975));
	}

	public Distribution getDistribution() {
		return d_distribution;
	}

	public String getName() {
		return "Network Meta-Analysis Relative Effect";
	}

	public boolean isDefined() {
		return true;
	}
}