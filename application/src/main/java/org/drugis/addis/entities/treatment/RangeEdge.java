package org.drugis.addis.entities.treatment;

import org.drugis.addis.util.BoundedInterval;


public class RangeEdge implements DecisionTreeEdge, Comparable<RangeEdge> {
	private static final String LESS_THAN = "\u003C";
	private static final String LESS_THAN_OR_EQUAL = "\u2264";
	private static final String GREATER_THAN = "\u003E";
	private static final String GREATER_THAN_OR_EQUAL = "\u2265";

	private final double d_lowerBound;
	private final boolean d_lowerBoundOpen;
	private final double d_upperBound;
	private final boolean d_upperBoundOpen;
	private final BoundedInterval d_boundedInterval;
	
	public static RangeEdge createDefault() {
		return new RangeEdge(0.0, false, Double.POSITIVE_INFINITY, true);
	}

	public static RangeEdge copy(RangeEdge edge) { 
		return new RangeEdge(edge.getLowerBound(), edge.isLowerBoundOpen(), edge.getUpperBound(), edge.isUpperBoundOpen());
	}
	
	/**
	 * Construct a RangeEdge that accepts or rejects Double values depending on whether they lie within the specified range.
	 * @param lowerBound Lower bound all property values should satisfy.
	 * @param isLowerBoundOpen True if the lower bound is open (exclusive), false if it is closed (inclusive).
	 * @param upperBound Upper bound all property values should satisfy.
	 * @param isUpperBoundOpen True if the upper bound is open (exclusive), false if it is closed (inclusive).
	 */
	public RangeEdge(
			final double lowerBound, final boolean isLowerBoundOpen,
			final double upperBound, final boolean isUpperBoundOpen) {
		d_lowerBound = lowerBound;
		d_lowerBoundOpen = isLowerBoundOpen;
		d_upperBound = upperBound;
		d_upperBoundOpen = isUpperBoundOpen;
		d_boundedInterval = new BoundedInterval(lowerBound, isLowerBoundOpen, upperBound, isUpperBoundOpen);
	}

	@Override
	public boolean decide(final Object object) {
		return d_boundedInterval.getRange().containsDouble((Double) object);
	}

	public double getLowerBound() {
		return d_lowerBound;
	}

	public boolean isLowerBoundOpen() {
		return d_lowerBoundOpen;
	}

	public double getUpperBound() {
		return d_upperBound;
	}

	public boolean isUpperBoundOpen() {
		return d_upperBoundOpen;
	}

	public BoundedInterval getInterval() {
		return d_boundedInterval;
	}

	@Override
	public int compareTo(final RangeEdge other) {
		if (getLowerBound() != other.getLowerBound()) {
			return getLowerBound() > other.getLowerBound() ? 1 : -1;
		} else if (getUpperBound() != other.getUpperBound()) {
			return getUpperBound() > other.getUpperBound() ? 1 : -1;
		} else {
			return 0;
		}
	}

	@Override
	public String toString() {
		return format("x", this);
	}

	public static String format(final String variableName, final RangeEdge range) {
		return format(variableName, range.getLowerBound(), range.isLowerBoundOpen(), range.getUpperBound(), range.isUpperBoundOpen());
	}

	public static String format(final String variableName,
			final double lowerBound, final boolean isLowerBoundOpen,
			final double upperBound, final boolean isUpperBoundOpen) {
		if (!Double.isInfinite(upperBound)) {
			return String.format("%.2f %s %s %s %.2f",
					lowerBound, isLowerBoundOpen ? LESS_THAN : LESS_THAN_OR_EQUAL,
					variableName,
					isUpperBoundOpen ? LESS_THAN : LESS_THAN_OR_EQUAL, upperBound);
		} else {
			return String.format("%s %s %.2f",
					variableName,
					isLowerBoundOpen ? GREATER_THAN : GREATER_THAN_OR_EQUAL, lowerBound);
		}

	}
}
