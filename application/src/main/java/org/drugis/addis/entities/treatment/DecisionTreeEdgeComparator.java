package org.drugis.addis.entities.treatment;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class DecisionTreeEdgeComparator implements Comparator<DecisionTreeEdge> {
	private static final List<Class<?>> s_types = Arrays.<Class<?>>asList(
		TypeEdge.class,
		RangeEdge.class
	);

	@Override
	public int compare(final DecisionTreeEdge o1, final DecisionTreeEdge o2) {
		final int i1 = s_types.indexOf(o1.getClass());
		final int i2 = s_types.indexOf(o2.getClass());
		if (i1 == i2 && o1 instanceof RangeEdge) {
			return ((RangeEdge)o1).compareTo((RangeEdge)o2);
		}
		return i2 - i1;
	}
}