package org.drugis.addis.entities.treatment;

import java.util.HashMap;
import java.util.Map;

import org.drugis.addis.entities.FlexibleDose;

public class TypeNode implements DecisionTreeNode {
	private final Map<Class<?>, DecisionTreeNode> d_nodeMap =
			new HashMap<Class<?>, DecisionTreeNode>();

	public TypeNode(Class<?> type, DecisionTreeNode child) {
		d_nodeMap.put(type, child);
	}
	
	@Override
	public DecisionTreeNode decide(Object object) {
		DecisionTreeNode node = d_nodeMap.get(object.getClass());	
		if (node == null) {
			throw new IllegalArgumentException("No category is present for the given class type.");
		}
		return node;
	}

	public void addType(Class<FlexibleDose> type, DecisionTreeNode node) {
		d_nodeMap.put(type, node);
	}
	
	@Override
	public boolean isLeaf() {
		return false;
	}

}
