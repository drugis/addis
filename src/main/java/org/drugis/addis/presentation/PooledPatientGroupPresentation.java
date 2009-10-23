package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.PooledPatientGroup;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class PooledPatientGroupPresentation extends PresentationModel<PooledPatientGroup> implements LabeledPresentationModel {
	public class LabelModel extends AbstractValueModel implements PropertyChangeListener {

		public String getValue() {
			return calcLabel();
		}
		
		private String calcLabel() {
			return "META " + getBean().getDrug().toString();
		}

		public void propertyChange(PropertyChangeEvent evt) {
			// IMMUTABLE
		}

		public void setValue(Object newValue) {
			throw new RuntimeException("Label is Read-Only");
		}
	}

	public PooledPatientGroupPresentation(PooledPatientGroup bean) {
		super(bean);
	}

	public AbstractValueModel getLabelModel() {
		return new LabelModel();
	}
}
