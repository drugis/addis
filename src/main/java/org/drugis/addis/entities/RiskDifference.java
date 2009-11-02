package org.drugis.addis.entities;

import org.drugis.addis.entities.Endpoint.Type;
import org.drugis.common.Interval;

public class RiskDifference extends Ratio implements ContinuousMeasurement {
	private static final long serialVersionUID = -6459490310869138478L;

	public RiskDifference(RateMeasurement denominator,
			RateMeasurement numerator) {
		super(denominator, numerator);
	}

	@Override
	public Double getRatio() {
		return 0.0;
	}
	
	@Override
	public Interval<Double> getConfidenceInterval() {
		return new Interval<Double>(0.0, 0.0);
	}

	@Override
	protected double getMean(RateMeasurement m) {
		return 0;
	}

	public boolean isOfType(Type type) {
		return type.equals(Type.CONTINUOUS);
	}

	public Double getMean() {
		return 0.0;
	}

	public Double getStdDev() {
		return 0.0;
	}
}
