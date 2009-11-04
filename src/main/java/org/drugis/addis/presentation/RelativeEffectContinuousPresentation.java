package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.RelativeEffectContinuous;
import org.drugis.common.Interval;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class RelativeEffectContinuousPresentation extends PresentationModel<RelativeEffectContinuous> implements LabeledPresentationModel {
	public class LabelModel extends AbstractValueModel implements PropertyChangeListener {
		public LabelModel() {
			getNumerator().getModel(ContinuousMeasurement.PROPERTY_MEAN).addPropertyChangeListener(this);
			getNumerator().getModel(ContinuousMeasurement.PROPERTY_STDDEV).addPropertyChangeListener(this);
			getNumerator().getModel(ContinuousMeasurement.PROPERTY_SAMPLESIZE).addPropertyChangeListener(this);
			getDenominator().getModel(ContinuousMeasurement.PROPERTY_MEAN).addPropertyChangeListener(this);
			getDenominator().getModel(ContinuousMeasurement.PROPERTY_STDDEV).addPropertyChangeListener(this);
			getDenominator().getModel(ContinuousMeasurement.PROPERTY_SAMPLESIZE).addPropertyChangeListener(this);
		}
		
		public void propertyChange(PropertyChangeEvent evt) {
			firePropertyChange("value", null, getValue());
		}

		public Object getValue() {
			DecimalFormat format = new DecimalFormat("0.00");
			Interval<Double> ci = getBean().getConfidenceInterval();
			return format.format(getBean().getRatio()) + " (" + format.format(ci.getLowerBound()) + ", " + 
				format.format(ci.getUpperBound()) + ")";
		}

		public void setValue(Object arg0) {
			throw new RuntimeException();
		}
	}
	private PresentationModelFactory d_pmm;

	public RelativeEffectContinuousPresentation(RelativeEffectContinuous bean, PresentationModelFactory pmm) {
		super(bean);
		d_pmm = pmm;
	}
	
	public AbstractValueModel getLabelModel() {
		return new LabelModel();
	}
	
	public PresentationModel<ContinuousMeasurement> getNumerator() {
		return d_pmm.getModel(getBean().getNumerator());
	}

	public PresentationModel<ContinuousMeasurement> getDenominator() {
		return d_pmm.getModel(getBean().getDenominator());
	}
}
