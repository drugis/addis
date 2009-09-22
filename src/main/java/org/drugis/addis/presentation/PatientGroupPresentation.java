package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;

import org.drugis.addis.entities.PatientGroup;

import com.jgoodies.binding.value.AbstractValueModel;

//FIXME: there should be separate implementations of this class for each concrete PatientGroup,
//and these should implement the PROPERTY_LABEL, in stead of the PatientGroup itself.

@SuppressWarnings("serial")
public class PatientGroupPresentation extends LabeledPresentationModel<PatientGroup> {
	public static class LabelModel extends AbstractLabelModel<PatientGroup> {
		public LabelModel(PatientGroup bean) {
			super(bean);
		}
		
		@Override
		public String getValue() {
			return getBean().getLabel();
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(PatientGroup.PROPERTY_LABEL)) {
				firePropertyChange("value", evt.getOldValue(), evt.getNewValue());
			}
		}
	}

	public PatientGroupPresentation(PatientGroup bean) {
		super(bean);
	}

	@Override
	public AbstractValueModel getLabelModel() {
		return new LabelModel(getBean());
	}

}
