package org.drugis.addis.presentation;

import org.drugis.addis.entities.Unit;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;

public class UnitPresentation extends PresentationModel<Unit> implements LabeledPresentation {
	public UnitPresentation(Unit bean) {
		super(bean);
	}

	private static final long serialVersionUID = -9033878838227050905L;

	public AbstractValueModel getLabelModel() {
		return new DefaultLabelModel(getBean());
	}

}
