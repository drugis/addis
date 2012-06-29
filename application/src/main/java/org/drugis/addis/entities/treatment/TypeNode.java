package org.drugis.addis.entities.treatment;

import org.apache.commons.math3.util.Pair;
import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.UnknownDose;
import org.drugis.common.beans.AbstractObservable;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

public class TypeNode extends AbstractObservable implements DecisionTreeNode {
	
	private ObservableList<Pair<Class<? extends AbstractDose>, DecisionTreeNode>> d_types = 
			new ArrayListModel<Pair<Class<? extends AbstractDose>,DecisionTreeNode>>();
	
	private TypeNode() {
	}
	
	public TypeNode(Class<? extends AbstractDose> type, DecisionTreeNode child) {
		d_types.add(new Pair<Class<? extends AbstractDose>, DecisionTreeNode>(type, child));
	}
	
	@Override
	public DecisionTreeNode decide(Object object) {
		try {
			int idx = findByType(object.getClass());
			DecisionTreeNode node = d_types.get(idx).getValue();
			return node;
		} catch(Exception e) {
			throw new IllegalArgumentException("No category is present for the given class type.");
		}
	}

	/**
	 * @param type the type to set the child of
	 * @param child, the desired child (may not be null)
	 * @return index of the child
	 */
	public int setType(Class<? extends AbstractDose> type, DecisionTreeNode child) {
		int idx = findByType(type);
		if (idx != -1) {
			d_types.set(idx, new Pair<Class<? extends AbstractDose>, DecisionTreeNode>(type, child));
		} else {
			d_types.add(new Pair<Class<? extends AbstractDose>, DecisionTreeNode>(type, child));
			idx = findByType(type);
		}
		return idx;
	}
	
	/**
	 * Set the child node for the index-th range.
	 * @param index Index of the range.
	 * @param node Desired child node.
	 * @throws IndexOutOfBoundsException If index < 0 or index >= {@link #getChildCount()}.
	 */
	public void setChildNode(int index, DecisionTreeNode node) {
		setType(d_types.get(index).getKey(), node);
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
		String result = "{";
		for (Pair<Class<? extends AbstractDose>, DecisionTreeNode> type : d_types) {
			result += type.getKey() + ": " + type.getValue().getName() + "; ";
		}
		result += "}";
		return result;
	}

	@Override
	public int getChildCount() {
		return d_types.size();
	}

	@Override
	public DecisionTreeNode getChildNode(int index) {
		return d_types.get(index).getValue();
	}
	
	private int findByType(Class<? extends Object> beanType) { 
		for(int idx = 0; idx < d_types.getSize(); ++idx) { 
			if(d_types.get(idx).getKey().equals(beanType)) { 
				return idx;
			}
		}
		return -1;
	}
	
}
