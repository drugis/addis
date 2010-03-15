package org.drugis.addis.entities;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.drugis.common.Interval;

public class LogContinuousMeasurementEstimate extends BasicContinuousMeasurement {
	private static final long serialVersionUID = -593325391463716636L;

	public LogContinuousMeasurementEstimate(double logMean, double logStdDev) {
		super(logMean, logStdDev, 0);
	}
	
	public Interval<Double> getConfidenceInterval() {
		NormalDistribution distribution = new NormalDistributionImpl(getMean(), getStdDev());
		try {
			return new Interval<Double>(Math.exp(distribution.inverseCumulativeProbability(0.025)),
					Math.exp(distribution.inverseCumulativeProbability(0.975)));
		} catch (MathException e) {
			e.printStackTrace();
			return null;
		}
	}

	public double getExpMean() {
		return Math.exp(getMean());
	}
}
