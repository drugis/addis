package org.drugis.addis.entities.treatment;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class DecisionTreeNodeComparator implements Comparator<DecisionTreeNode> {
	private static final List<Class<?>> s_types = Arrays.<Class<?>>asList(
		EmptyNode.class,
		CategoryNode.class,
		ExcludeNode.class,
		TypeNode.class,
		RangeNode.class,
		DoseRangeNode.class
	);

	@Override
	public int compare(DecisionTreeNode o1, DecisionTreeNode o2) {
		int i1 = s_types.indexOf(o1.getClass());
		int i2 = s_types.indexOf(o2.getClass());
		if (i1 == i2) {
			if (o1 instanceof RangeNode) {
				return ((RangeNode)o1).compareTo((RangeNode)o2);
			} else {
				return System.identityHashCode(o2) - System.identityHashCode(o1);
			}
		}
		return i2 - i1;
	}
}