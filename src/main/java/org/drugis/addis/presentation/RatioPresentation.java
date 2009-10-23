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
public class RatioPresentation extends PresentationModel<Ratio> implements LabeledPresentationModel {
	public class LabelModel implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			firePropertyChange(PROPERTY_LABEL, null, getLabel());
		}
	}

	private PresentationModelManager d_pmm;
	private LabelModel d_labelModel;

	public RatioPresentation(Ratio bean, PresentationModelManager pmm) {
		super(bean);
		d_pmm = pmm;
		d_labelModel = new LabelModel();
		
		getNumerator().getModel(RateMeasurement.PROPERTY_RATE).addPropertyChangeListener(d_labelModel);
		getNumerator().getModel(RateMeasurement.PROPERTY_SAMPLESIZE).addPropertyChangeListener(d_labelModel);
		getDenominator().getModel(RateMeasurement.PROPERTY_RATE).addPropertyChangeListener(d_labelModel);
		getDenominator().getModel(RateMeasurement.PROPERTY_SAMPLESIZE).addPropertyChangeListener(d_labelModel);
	}

	public AbstractValueModel getLabelModel() {
		return getModel(PROPERTY_LABEL);
	}
	
	public PresentationModel<RateMeasurement> getNumerator() {
		return d_pmm.getModel(getBean().getNumerator());
	}

	public PresentationModel<RateMeasurement> getDenominator() {
		return d_pmm.getModel(getBean().getDenominator());
	}

	public String getLabel() {
		DecimalFormat format = new DecimalFormat("0.00");
		Interval<Double> ci = getBean().getConfidenceInterval();
		return format.format(getBean().getRatio()) + " (" + format.format(ci.getLowerBound()) + "-" + 
			format.format(ci.getUpperBound()) + ")";
	}
}
