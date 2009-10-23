package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.Dose;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.PatientGroup;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class BasicPatientGroupPresentation extends PresentationModel<BasicPatientGroup> implements LabeledPresentationModel {
	public class LabelModel extends AbstractValueModel implements PropertyChangeListener {
		private String d_cachedLabel;
		
		public LabelModel() {
			d_cachedLabel = calcLabel(getDrug(), getDose());
			getBean().addPropertyChangeListener(this);
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
	}

	public BasicPatientGroupPresentation(BasicPatientGroup bean) {
		super(bean);
	}

	public AbstractValueModel getLabelModel() {
		return new LabelModel();
	}
}
