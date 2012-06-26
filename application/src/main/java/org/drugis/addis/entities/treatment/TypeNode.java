package org.drugis.addis.entities.treatment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.UnknownDose;
import org.drugis.addis.entities.FixedDose;
import org.drugis.common.beans.AbstractObservable;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

public class TypeNode extends AbstractObservable implements DecisionTreeNode {
	
	private final Map<Class<? extends AbstractDose>, DecisionTreeNode> d_nodeMap =
			new HashMap<Class<? extends AbstractDose>, DecisionTreeNode>();
	
	private ObservableList<Class<? extends AbstractDose>> d_types = new ArrayListModel<Class<? extends AbstractDose>>();
	
	private TypeNode() {
	}
	
	public TypeNode(Class<? extends AbstractDose> type, DecisionTreeNode child) {
		d_nodeMap.put(type, child);
		d_types.add(type);
	}
	
	@Override
	public DecisionTreeNode decide(Object object) {
		DecisionTreeNode node = d_nodeMap.get(object.getClass());	
		if (node == null) {
			throw new IllegalArgumentException("No category is present for the given class type.");
		}
		return node;
	}

	public void setType(Class<? extends AbstractDose> type, DecisionTreeNode child) {
		d_nodeMap.put(type, child);
		d_types.remove(type);
		d_types.add(type);
	}
	
	public Map<Class<? extends AbstractDose>, DecisionTreeNode> getTypeMap() { 
		return Collections.unmodifiableMap(d_nodeMap);
	}
	
	public ObservableList<Class<? extends AbstractDose>> getTypes() { 
		return d_types;
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

	@Override
	public String getName() {
		return getTypeMap().toString();
	}
}
