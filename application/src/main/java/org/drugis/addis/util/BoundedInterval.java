package org.drugis.addis.util;

import org.apache.commons.lang.math.DoubleRange;
import org.drugis.common.EqualsUtil;

public class BoundedInterval {
	private final DoubleRange d_range;
	private final boolean d_lowerBoundIsOpen;
	private final boolean d_upperBoundIsOpen;
	public static final double EPSILON = 1.0E-14;
	
	public BoundedInterval(DoubleRange range, boolean lowerBoundIsOpen, boolean upperBoundIsOpen) {
		d_range = range;
		d_lowerBoundIsOpen = lowerBoundIsOpen;
		d_upperBoundIsOpen = upperBoundIsOpen;
	}

	public BoundedInterval(double lowerBound, boolean lowerBoundIsOpen, double upperBound, boolean upperBoundIsOpen) {
		this(new DoubleRange(
				 lowerBound + (lowerBoundIsOpen ? BoundedInterval.EPSILON : 0), 
				 upperBound	- (upperBoundIsOpen ? BoundedInterval.EPSILON : 0)),
				 lowerBoundIsOpen,
				 upperBoundIsOpen);
	}

	public DoubleRange getRange() {
		return d_range;
	}

	public boolean isLowerBoundOpen() {
		return d_lowerBoundIsOpen;
	}

	public boolean isUpperBoundOpen() {
		return d_upperBoundIsOpen;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BoundedInterval) {
			BoundedInterval other = (BoundedInterval) obj;
			return EqualsUtil.equal(d_range, other.d_range) &&
					d_lowerBoundIsOpen == other.d_lowerBoundIsOpen &&
					d_upperBoundIsOpen == other.d_upperBoundIsOpen;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return d_range.hashCode() + 31 * (d_lowerBoundIsOpen ? 1 : 0) + 31 * 31 * (d_upperBoundIsOpen ? 1 : 0);
	}
	
}