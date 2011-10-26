package org.drugis.addis.forestplot;

import org.drugis.common.Interval;

public abstract class ScaleBase implements Scale {

	protected final Interval<Double> d_in;

	public ScaleBase(Interval<Double> interval) {
		d_in = interval;
	}

	public double getMax() {
		return d_in.getUpperBound();
	}

	public double getMin() {
		return d_in.getLowerBound();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getMin() + ", " + getMax() + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ScaleBase) {
			ScaleBase other = (ScaleBase)obj;
			return canEqual(other) && d_in.equals(other.d_in);
		}
		return false;
	}

	protected abstract boolean canEqual(ScaleBase other);
	
	@Override
	public int hashCode() {
		return d_in.hashCode();
	}

}
