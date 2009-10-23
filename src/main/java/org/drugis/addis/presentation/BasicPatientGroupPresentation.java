package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.Dose;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.PatientGroup;

import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class BasicPatientGroupPresentation extends LabeledPresentationModel<BasicPatientGroup> {
	public static class LabelModel extends AbstractLabelModel<PatientGroup> {
		private String d_cachedLabel;
		protected PatientGroup d_bean;
		
		public LabelModel(BasicPatientGroup bean) {
			d_bean = bean;
			d_cachedLabel = calcLabel(getDrug(), getDose());
			bean.addPropertyChangeListener(this);
		}
		
		private String calcLabel(Drug drug, Dose dose) {
			if (drug == null || dose == null) {
				return "INCOMPLETE";
			}
			return drug.toString() + " " + dose.toString();
		}

		private Dose getDose() {
			return getBean().getDose();
		}

		private Drug getDrug() {
			return getBean().getDrug();
		}

		public String getValue() {
			return d_cachedLabel;
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(PatientGroup.PROPERTY_DRUG)) {
				String oldVal = d_cachedLabel;
				d_cachedLabel = calcLabel((Drug)evt.getNewValue(), getDose());
				firePropertyChange("value", oldVal, d_cachedLabel);
			} else if (evt.getPropertyName().equals(PatientGroup.PROPERTY_DOSE)) {
				String oldVal = d_cachedLabel;
				d_cachedLabel = calcLabel(getDrug(), (Dose)evt.getNewValue());
				firePropertyChange("value", oldVal, d_cachedLabel);
			}
		}

		public void setValue(Object newValue) {
			throw new RuntimeException("Label is Read-Only");
		}

		protected void firePropertyChange(String oldVal, String newVal) {
			firePropertyChange("value", oldVal, newVal);
		}

		protected PatientGroup getBean() {
			return d_bean;
		}
	}

	public BasicPatientGroupPresentation(BasicPatientGroup bean) {
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
