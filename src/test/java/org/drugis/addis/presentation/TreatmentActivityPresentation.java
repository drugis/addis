package org.drugis.addis.presentation;

import org.drugis.addis.entities.TreatmentActivity;

import com.jgoodies.binding.PresentationModel;

@SuppressWarnings("serial")
public class TreatmentActivityPresentation extends PresentationModel<TreatmentActivity> {

	public TreatmentActivityPresentation(TreatmentActivity bean, PresentationModelFactory pmf) {
		super(bean);
	}

	public DosePresentation getDoseModel() {
		return new DosePresentationImpl(this);
	}
}
