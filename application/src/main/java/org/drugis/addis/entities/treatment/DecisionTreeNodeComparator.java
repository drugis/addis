package org.drugis.addis.entities.treatment;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class DecisionTreeNodeComparator implements Comparator<DecisionTreeNode> {
	private static final List<Class<?>> s_types = Arrays.<Class<?>>asList(
		LeafNode.class,
		ChoiceNode.class,
		DoseQuantityChoiceNode.class
	);

	@Override
	public int compare(final DecisionTreeNode o1, final DecisionTreeNode o2) {
		final int i1 = s_types.indexOf(o1.getClass());
		final int i2 = s_types.indexOf(o2.getClass());
		return i2 - i1;
	}
}