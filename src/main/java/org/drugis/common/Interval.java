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
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Interval<?>) {
			Interval<?> in = (Interval<?>) o;
			if (in.getLowerBound().getClass().equals(getLowerBound().getClass())) {
				return ((getLowerBound().equals(in.getLowerBound())) && (getUpperBound().equals(in.getUpperBound())));
			}
		} 
		
		return false;
		
	}
	
	@Override
	public String toString() {
		return getLowerBound().toString() + "-" + getUpperBound().toString();
	}
}
