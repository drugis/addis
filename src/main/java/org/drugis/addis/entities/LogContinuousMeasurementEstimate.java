package org.drugis.addis.entities;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.drugis.common.Interval;

public class LogContinuousMeasurementEstimate extends ContinuousMeasurementEstimate { //ContinuousMeasurementEstimate {
	private static final long serialVersionUID = -593325391463716636L;

	public LogContinuousMeasurementEstimate(Double logMean, Double logStdDev) {
		super(logMean, logStdDev);
	}
	
	@Override
	public Interval<Double> getConfidenceInterval() {
		NormalDistribution distribution = new NormalDistributionImpl(getMean(), super.getStdDev());
		try {
			return new Interval<Double>(Math.exp(distribution.inverseCumulativeProbability(0.025)),
					Math.exp(distribution.inverseCumulativeProbability(0.975)));
		} catch (MathException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String toString() {
		if (getMean() == null || getStdDev() == null)
			return "n/a"; 
		
		return d_decimalFormatter.format(Math.exp(getMean())) + " (" + d_decimalFormatter.format(getConfidenceInterval().getLowerBound())
				+ ", " + d_decimalFormatter.format(getConfidenceInterval().getUpperBound()) + ")";
	}
}
