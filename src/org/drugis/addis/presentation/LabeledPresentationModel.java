package org.drugis.addis.presentation;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public abstract class LabeledPresentationModel<B> extends PresentationModel<B> {
	public static final String PROPERTY_LABEL = "label";

	public LabeledPresentationModel(B bean) {
		super(bean);
	}
	
	public abstract AbstractValueModel getLabelModel();
	
	public AbstractValueModel getModel(String name) { 
		if (PROPERTY_LABEL.equals(name)) {
			return getLabelModel();
		}
		return super.getModel(name);
	}
}
