package org.drugis.addis.entities;

import org.drugis.common.Interval;

public interface Distribution {
	
	public enum AxisType {
		LINEAR,
		LOGARITHMIC;
	}

	public Double getSigma();

	public Double getMu();

	public AxisType getAxisType();

	public Double getMedian();
	
	public Interval<Double> getConfidenceInterval();

}