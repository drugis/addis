package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.PooledPatientGroup;

import com.jgoodies.binding.value.AbstractValueModel;

//FIXME: there should be separate implementations of this class for each concrete PatientGroup,
//and these should implement the PROPERTY_LABEL, in stead of the PatientGroup itself.

@SuppressWarnings("serial")
public class PooledPatientGroupPresentation extends LabeledPresentationModel<PooledPatientGroup> {
	public static class LabelModel extends AbstractLabelModel<PooledPatientGroup> {
		protected PooledPatientGroup d_bean;

		public LabelModel(PooledPatientGroup bean) {
			d_bean = bean;
		}
		
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

		protected void firePropertyChange(String oldVal, String newVal) {
			firePropertyChange("value", oldVal, newVal);
		}

		protected PooledPatientGroup getBean() {
			return d_bean;
		}
	}

	public PooledPatientGroupPresentation(PooledPatientGroup bean) {
		super(bean);
		//d_pmm = pmm;
		getLabelModel().addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				firePropertyChange(PROPERTY_LABEL, evt.getOldValue(), evt.getNewValue());
			}
		});
	}

	@Override
	public AbstractValueModel getLabelModel() {
		return new LabelModel(getBean());
	}

}
