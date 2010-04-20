package org.drugis.addis.entities;

import java.text.DecimalFormat;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.drugis.common.Interval;

@SuppressWarnings("serial")
public class ContinuousMeasurementEstimate extends BasicContinuousMeasurement{
	protected DecimalFormat d_decimalFormatter;

	public ContinuousMeasurementEstimate(Double mean, Double stddev){
		super(mean, stddev, 0);
		d_decimalFormatter = new DecimalFormat("##0.0##");
	}
	
	public Interval<Double> getConfidenceInterval() {
		NormalDistribution distribution = new NormalDistributionImpl(getMean(), getStdDev());
		try {
			return new Interval<Double>(distribution.inverseCumulativeProbability(0.025),
					distribution.inverseCumulativeProbability(0.975));
		} catch (MathException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String toString() {
		if (getMean() == null || getStdDev() == null)
			return "n/a"; 
		
		return d_decimalFormatter.format(getMean()) + " (" + d_decimalFormatter.format(getConfidenceInterval().getLowerBound())
				+ ", " + d_decimalFormatter.format(getConfidenceInterval().getUpperBound()) + ")";
	}
}
