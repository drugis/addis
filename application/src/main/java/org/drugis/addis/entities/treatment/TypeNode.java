package org.drugis.addis.entities.treatment;

import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.gui.GUIHelper;

public class TypeNode extends DecisionTreeNode {
	private ValueHolder<Class<? extends Object>> d_type = new ModifiableHolder<Class<? extends Object>>();
	
	public TypeNode(Class<? extends Object> type) {
		d_type.setValue(type);
	}
	
	@Override
	public boolean decide(Object object) {
		return d_type.getValue().equals(object.getClass());
	}

	public void setType(Class<? extends Object> type) {
		d_type.setValue(type);
	}
	
	@Override
	public String getName() {
		return GUIHelper.humanize(d_type.getValue().getSimpleName());
	}
}
