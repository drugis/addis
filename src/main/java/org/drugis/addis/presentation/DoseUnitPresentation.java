package org.drugis.addis.presentation;

import org.drugis.addis.entities.DoseUnit;

import com.jgoodies.binding.PresentationModel;

public class DoseUnitPresentation extends PresentationModel<DoseUnit> {
	private static final long serialVersionUID = -6098428528138487645L;

	public DoseUnitPresentation(DoseUnit bean) {
		super(bean);
	}

	public DurationPresentation<DoseUnit> getDurationPresentation() {
		return new DurationPresentation<DoseUnit>(getBean());
	}

}
