package org.drugis.addis.entities.relativeeffect;

import java.text.DecimalFormat;

import org.drugis.common.Interval;

public class ConfidenceInterval extends Interval<Double> {

	private final Double d_pointEstimate;

	public ConfidenceInterval(Double pointEstimate, Double lowerBound, Double upperBound) {
		super(lowerBound, upperBound);
		d_pointEstimate = pointEstimate;
	}

	public Double getPointEstimate() {
		return d_pointEstimate;
	}

	public String toString() {
		DecimalFormat format = new DecimalFormat("###0.00");
		return format.format(getPointEstimate()) + " (" + format.format(getLowerBound()) + ", " + 
			format.format(getUpperBound()) + ")";
	}
}
