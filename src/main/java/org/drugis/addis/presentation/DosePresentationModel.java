package org.drugis.addis.presentation;

import com.jgoodies.binding.value.AbstractValueModel;

public interface DosePresentationModel {
	public AbstractValueModel getMinModel();
	public AbstractValueModel getMaxModel();
	public AbstractValueModel getUnitModel();
}
