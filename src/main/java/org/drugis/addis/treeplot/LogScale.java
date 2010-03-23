package org.drugis.addis.treeplot;

import org.drugis.common.Interval;

public class LogScale implements Scale {

	private double d_max;
	private double d_min;

	public LogScale(Interval<Double> interval) {
		d_max = interval.getUpperBound();
		d_min = interval.getLowerBound();
	}

	public double getMax() {
		return d_max;
	}

	public double getMin() {
		return d_min;
	}

	public double getNormalized(double x) {
		return Math.log(x / d_min) / Math.log(d_max / d_min); 
	}
	
	public double getNormalizedLog10(double x) {
		return Math.log10(x / d_min) / Math.log10(d_max / d_min); 
	}
}
