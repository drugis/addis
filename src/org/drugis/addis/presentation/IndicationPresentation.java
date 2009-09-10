package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;

import org.drugis.addis.entities.Indication;

import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class IndicationPresentation extends LabeledPresentationModel<Indication> {
	private static class LabelModel extends AbstractLabelModel<Indication> {
		protected LabelModel(Indication bean) {
			super(bean);
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(Indication.PROPERTY_CODE)) {
				firePropertyChange(evt.getOldValue() + " " + getBean().getName(), getValue());
			} else if (evt.getPropertyName().equals(Indication.PROPERTY_NAME)) {
				firePropertyChange(getBean().getCode() + " " + evt.getOldValue(), getValue());
			}
		}
	}

	public IndicationPresentation(Indication bean) {
		super(bean);
	}

	@Override
	public AbstractValueModel getLabelModel() {
		return new LabelModel(getBean());
	}

}
