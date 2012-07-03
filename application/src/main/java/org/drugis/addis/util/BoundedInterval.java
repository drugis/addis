package org.drugis.addis.util;

import org.apache.commons.lang.math.DoubleRange;
import org.drugis.addis.entities.treatment.RangeNode;

public class BoundedInterval {
	private final DoubleRange d_range;
	private final boolean d_lowerBoundIsOpen;
	private final boolean d_upperBoundIsOpen;
	
	public BoundedInterval(DoubleRange range, boolean lowerBoundIsOpen, boolean upperBoundIsOpen) {
		d_range = range;
		d_lowerBoundIsOpen = lowerBoundIsOpen;
		d_upperBoundIsOpen = upperBoundIsOpen;
	}

	public BoundedInterval(double lowerBound, boolean lowerBoundIsOpen, double upperBound, boolean upperBoundIsOpen) {
		this(new DoubleRange(
				 lowerBound + (lowerBoundIsOpen ? RangeNode.EPSILON : 0), 
				 upperBound	- (upperBoundIsOpen ? RangeNode.EPSILON : 0)),
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
}