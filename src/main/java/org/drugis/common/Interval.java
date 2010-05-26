package org.drugis.common;


public class Interval<N extends Number> {
	
	private N d_lowerBound;
	private N d_upperBound;
	
	public Interval(N lowerBound, N upperBound) {
		d_lowerBound = lowerBound;
		d_upperBound = upperBound;
	}
	
	public N getLowerBound() {
		return d_lowerBound;
	}
	
	public N getUpperBound() {
		return d_upperBound;
	}
	
	public double getLength() {
		return d_upperBound.doubleValue() - d_lowerBound.doubleValue();
	}
	
	protected boolean canEqual(Interval<?> other) {
		if (other.getClass().equals(Interval.class)) return true;
		return false;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Interval<?>) {
			Interval<?> other = (Interval<?>) o;
			if (other.canEqual(this)) {
				if (other.getLowerBound().getClass().equals(getLowerBound().getClass())) {
					return ((getLowerBound().equals(other.getLowerBound())) && (getUpperBound().equals(other.getUpperBound())));
				}
			}
		} 
		
		return false;
		
	}
	
	@Override public int hashCode() {
		return d_lowerBound.hashCode() * 31 + d_upperBound.hashCode();
	}
	
	@Override
	public String toString() {
		return getLowerBound().toString() + "-" + getUpperBound().toString();
	}
}
