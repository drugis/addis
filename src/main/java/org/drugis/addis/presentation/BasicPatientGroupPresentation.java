package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;

import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.PatientGroup;

import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class BasicPatientGroupPresentation extends LabeledPresentationModel<BasicPatientGroup> {
	public static class LabelModel extends AbstractLabelModel<PatientGroup> {
		public LabelModel(BasicPatientGroup bean) {
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

	public BasicPatientGroupPresentation(BasicPatientGroup bean) {
		super(bean);
	}

	@Override
	public AbstractValueModel getLabelModel() {
		return new LabelModel(getBean());
	}

}
