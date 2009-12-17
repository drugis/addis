package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.TreeSet;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicArm;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Variable;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class BasicArmPresentation extends PresentationModel<Arm> implements LabeledPresentationModel {
	public class LabelModel extends AbstractValueModel implements PropertyChangeListener {
		private String d_cachedLabel;
		
		public LabelModel() {
			d_cachedLabel = calcLabel(getDrug());
			getBean().addPropertyChangeListener(this);
		}
		
		private String calcLabel(Drug drug) {
			if (drug == null) {
				return "INCOMPLETE";
			}
			return drug.toString();
		}

		private Drug getDrug() {
			return getBean().getDrug();
		}

		public String getValue() {
			return d_cachedLabel;
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(Arm.PROPERTY_DRUG)) {
				String oldVal = d_cachedLabel;
				d_cachedLabel = calcLabel((Drug)evt.getNewValue());
				firePropertyChange("value", oldVal, d_cachedLabel);
			}
		}

		public void setValue(Object newValue) {
			throw new RuntimeException("Label is Read-Only");
		}
	}

	private PresentationModelFactory d_pmf;

	public BasicArmPresentation(BasicArm bean, PresentationModelFactory pmf) {
		super(bean);
		d_pmf = pmf;
	}

	public AbstractValueModel getLabelModel() {
		return new LabelModel();
	}
	
	public DosePresentationModel getDoseModel() {
		return new DosePresentationImpl(this);
	}
	
	public String getCharacteristicTooltip() {
		if (getBean().getCharacteristics().isEmpty())
			return "";
		
		String ret = new String("<html>");
		for (Variable c : new TreeSet<Variable>(getBean().getCharacteristics().keySet())) {
			Object val = getBean().getCharacteristic(c);
			if (val != null)
				ret += c.getName() + ": " + val + "<br>";			
		}
		ret += "</html>";
		return ret;
		
	}

	public LabeledPresentationModel getCharacteristicModel(Variable v) {
		if (getBean().getCharacteristic(v) != null) {
			return d_pmf.getLabeledModel(getBean().getCharacteristic(v));
		}
		return null;
	}
}
