package org.drugis.addis.entities.treatment;

import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.EqualsUtil;

public class TypeNode extends DecisionTreeNode {
	public static final String PROPERTY_TYPE = "type";
	
	private ValueHolder<Class<? extends AbstractDose>> d_type = new ModifiableHolder<Class<? extends AbstractDose>>();
	
	public TypeNode(Class<? extends AbstractDose> type) {
		d_type.setValue(type);
	}
	
	@Override
	public boolean decide(Object object) {
		return d_type.getValue().equals(object.getClass());
	}

	public void setType(Class<? extends AbstractDose> type) {
		d_type.setValue(type);
	}
	
	@Override
	public String getName() {
//		return GUIHelper.humanize(d_type.getValue().getSimpleName());
		return d_type.getValue().getSimpleName();
	}
	
	public ValueHolder<Class<? extends AbstractDose>> getType() {
		return d_type;
	}
	
	@Override
	public Class<?> getBeanClass() {
		return d_type.getValue();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TypeNode) {
			TypeNode other = (TypeNode) obj;
			return EqualsUtil.equal(d_type.getValue(), other.d_type.getValue());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return d_type.getValue().hashCode();
	}

	@Override
	public String getPropertyName() {
		return getBeanClass().getSimpleName();
	}
	
	public boolean similar(DecisionTreeNode other) {
		if(other instanceof TypeNode) { 
			return ((TypeNode)other).getType().equals(getType());
		}
		return false;
	}
}
