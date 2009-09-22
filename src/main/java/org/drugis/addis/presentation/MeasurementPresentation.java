package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;

import org.drugis.addis.entities.Measurement;

import com.jgoodies.binding.value.AbstractValueModel;

// FIXME: there should be separate implementations of this class for each concrete Measurement,
// and these should implement the PROPERTY_LABEL, in stead of the Measurement itself.

@SuppressWarnings("serial")
public class MeasurementPresentation extends LabeledPresentationModel<Measurement> {
	public static class LabelModel extends AbstractLabelModel<Measurement> {
		public LabelModel(Measurement bean) {
			super(bean);
		}
		
		@Override
		public String getValue() {
			return getBean().getLabel();
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(Measurement.PROPERTY_LABEL)) {
				firePropertyChange("value", evt.getOldValue(), evt.getNewValue());
			}
		}
	}

	public MeasurementPresentation(Measurement bean) {
		super(bean);
	}

	@Override
	public AbstractValueModel getLabelModel() {
		return new LabelModel(getBean());
	}

}
