package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.Ratio;
import org.drugis.common.Interval;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;

// FIXME: there should be separate implementations of this class for each concrete Measurement,
// and these should implement the PROPERTY_LABEL, in stead of the Measurement itself.

@SuppressWarnings("serial")
public class RatioPresentation extends LabeledPresentationModel<Ratio> {
	
	public static class LabelModel extends AbstractValueModel implements PropertyChangeListener {
		private RatioPresentation d_bean;

		public LabelModel(RatioPresentation bean) {
			d_bean = bean;
			getBean().getNumerator().getModel(RateMeasurement.PROPERTY_RATE).addPropertyChangeListener(this);
			getBean().getNumerator().getModel(RateMeasurement.PROPERTY_SAMPLESIZE).addPropertyChangeListener(this);
			getBean().getDenominator().getModel(RateMeasurement.PROPERTY_RATE).addPropertyChangeListener(this);
			getBean().getDenominator().getModel(RateMeasurement.PROPERTY_SAMPLESIZE).addPropertyChangeListener(this);
		}
		
		public RatioPresentation getBean() {
			return d_bean;
		}

		public String getValue() {
			return getLabel();
		}
		
		public String getLabel() {
			DecimalFormat format = new DecimalFormat("0.00");
			Interval<Double> ci = getBean().getBean().getConfidenceInterval();
			return format.format(getBean().getBean().getRatio()) + " (" + format.format(ci.getLowerBound()) + "-" + 
					format.format(ci.getUpperBound()) + ")";
		}

		public void propertyChange(PropertyChangeEvent evt) {
				firePropertyChange("value", null, getValue());
		}

		public void setValue(Object arg0) {
			throw new RuntimeException("Value read-only");
		}
	}

	public RatioPresentation(Ratio bean, PresentationModelManager pmm) {
		super(bean);
		d_pmm = pmm;
		getLabelModel().addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				firePropertyChange(PROPERTY_LABEL, evt.getOldValue(), evt.getNewValue());
			}
		});
	}

	@Override
	public AbstractValueModel getLabelModel() {
		return new LabelModel(this);
	}
	
	public PresentationModel<RateMeasurement> getNumerator() {
		return d_pmm.getModel(getBean().getNumerator());
	}

	public PresentationModel<RateMeasurement> getDenominator() {
		return d_pmm.getModel(getBean().getDenominator());
	}
}
