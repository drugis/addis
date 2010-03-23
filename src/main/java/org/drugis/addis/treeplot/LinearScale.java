package org.drugis.addis.treeplot;

import org.drugis.common.Interval;

public class LinearScale implements Scale {
	
	Interval<Double> d_in;

	public LinearScale(Interval<Double> interval) {
		d_in = interval;
	}
	
	public double getMax() {
		return d_in.getUpperBound();
	}

	public double getMin() {
		return d_in.getLowerBound();
	}

	public double getNormalized(double x) {
		return (x - getMin()) / (getMax() - getMin());
	}

}
