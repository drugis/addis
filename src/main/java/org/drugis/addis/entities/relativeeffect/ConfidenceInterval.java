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
	
	protected boolean canEqual(Interval<?> other) {
		if (other.getClass().equals(ConfidenceInterval.class)) return true;
		return false;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ConfidenceInterval) {
			ConfidenceInterval other = (ConfidenceInterval) o;
			if (other.canEqual(this)) {
				return other.d_pointEstimate.equals(d_pointEstimate) && other.getLowerBound().equals(getLowerBound()) &&
					other.getUpperBound().equals(getUpperBound());
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode() * 31 + d_pointEstimate.hashCode();
	}

	@Override
	public String toString() {
		DecimalFormat format = new DecimalFormat("###0.00");
		return format.format(getPointEstimate()) + " (" + format.format(getLowerBound()) + ", " + 
			format.format(getUpperBound()) + ")";
	}
}
