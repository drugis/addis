package org.drugis.addis.entities.relativeeffect;

import org.drugis.addis.entities.AbstractObservable;
import org.drugis.addis.entities.Measurement;

public abstract class AbstractRelativeEffect<T extends Measurement> extends AbstractObservable implements RelativeEffect<T>{

	public AxisType getAxisType() {
		return getDistribution().getAxisType();
	}

	public ConfidenceInterval getConfidenceInterval() {
		if (!isDefined()) {
			return new ConfidenceInterval(Double.NaN, Double.NaN, Double.NaN);
		}
	
		return new ConfidenceInterval(getDistribution().getQuantile(0.5), getDistribution().getQuantile(0.025), getDistribution().getQuantile(0.975));
	}
}
