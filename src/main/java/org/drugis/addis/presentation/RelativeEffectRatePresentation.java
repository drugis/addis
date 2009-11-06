package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.RelativeEffectRate;
import org.drugis.common.Interval;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class RelativeEffectRatePresentation extends PresentationModel<RelativeEffectRate> implements LabeledPresentationModel {
	public class LabelModel extends AbstractValueModel implements PropertyChangeListener {
		public LabelModel() {
			getNumerator().getModel(RateMeasurement.PROPERTY_RATE).addPropertyChangeListener(this);
			getNumerator().getModel(RateMeasurement.PROPERTY_SAMPLESIZE).addPropertyChangeListener(this);
			getDenominator().getModel(RateMeasurement.PROPERTY_RATE).addPropertyChangeListener(this);
			getDenominator().getModel(RateMeasurement.PROPERTY_SAMPLESIZE).addPropertyChangeListener(this);
		}
		
		public void propertyChange(PropertyChangeEvent evt) {
			firePropertyChange("value", null, getValue());
		}

		public Object getValue() {
			DecimalFormat format = new DecimalFormat("0.00");
			Interval<Double> ci = getBean().getConfidenceInterval();
			return format.format(getBean().getRelativeEffect()) + " (" + format.format(ci.getLowerBound()) + ", " + 
				format.format(ci.getUpperBound()) + ")";
		}

		public void setValue(Object arg0) {
			throw new RuntimeException();
		}
	}

	private PresentationModelFactory d_pmm;

	public RelativeEffectRatePresentation(RelativeEffectRate bean, PresentationModelFactory pmm) {
		super(bean);
		d_pmm = pmm;
	}
	
	public AbstractValueModel getLabelModel() {
		return new LabelModel();
	}
	
	public PresentationModel<RateMeasurement> getNumerator() {
		return d_pmm.getModel(getBean().getSubject());
	}

	public PresentationModel<RateMeasurement> getDenominator() {
		return d_pmm.getModel(getBean().getBaseline());
	}
}
