package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;

import org.drugis.addis.entities.Ratio;

import com.jgoodies.binding.value.AbstractValueModel;

// FIXME: there should be separate implementations of this class for each concrete Measurement,
// and these should implement the PROPERTY_LABEL, in stead of the Measurement itself.

@SuppressWarnings("serial")
public class RatioPresentation extends LabeledPresentationModel<Ratio> {
	public static class LabelModel extends AbstractLabelModel<Ratio> {
		public LabelModel(Ratio bean) {
			super(bean);
		}
		
		@Override
		@SuppressWarnings("deprecation")
		public String getValue() {
			return getBean().getLabel();
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(Ratio.PROPERTY_LABEL)) {
				firePropertyChange("value", evt.getOldValue(), evt.getNewValue());
			}
		}
	}

	public RatioPresentation(Ratio bean) {
		super(bean);
	}

	@Override
	public AbstractValueModel getLabelModel() {
		return new LabelModel(getBean());
	}

}
