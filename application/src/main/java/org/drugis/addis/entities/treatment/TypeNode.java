package org.drugis.addis.entities.treatment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.UnknownDose;
import org.drugis.addis.entities.FixedDose;

public class TypeNode implements DecisionTreeNode {
	private final Map<Class<? extends AbstractDose>, DecisionTreeNode> d_nodeMap =
			new HashMap<Class<? extends AbstractDose>, DecisionTreeNode>();

	private TypeNode() {
	}
	
	public TypeNode(Class<? extends AbstractDose> type, DecisionTreeNode child) {
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

	public void setType(Class<? extends AbstractDose> type, DecisionTreeNode node) {
		d_nodeMap.put(type, node);
	}
	
	public Map<Class<? extends AbstractDose>, DecisionTreeNode> getTypeMap() { 
		return Collections.unmodifiableMap(d_nodeMap);
	}
	
	@Override
	public boolean isLeaf() {
		return false;
	}

	public static TypeNode createDefaultTypeNode() {
		TypeNode node = new TypeNode();
		ExcludeNode exclude = new ExcludeNode();
		
		node.setType(UnknownDose.class, exclude);
		node.setType(FixedDose.class, exclude);
		node.setType(FlexibleDose.class, exclude);
		
		return node;
	}
}
