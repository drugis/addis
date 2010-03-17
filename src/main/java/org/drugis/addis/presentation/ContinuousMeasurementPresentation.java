package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.common.Interval;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;

// FIXME: there should be separate implementations of this class for each concrete Measurement,
// and these should implement the PROPERTY_LABEL, in stead of the Measurement itself.

@SuppressWarnings("serial")
public class ContinuousMeasurementPresentation<T extends ContinuousMeasurement> 
extends PresentationModel<T> implements LabeledPresentationModel {
	public class LabelModel extends  AbstractValueModel implements PropertyChangeListener {
		public LabelModel() {
			getBean().addPropertyChangeListener(this);
		}
		
		public String getValue() {
			return getBean().toString();
		}
		
		public void propertyChange(PropertyChangeEvent evt) {
			firePropertyChange("value", null, getBean().toString());
		}

		public void setValue(Object newValue) {
			throw new RuntimeException("Label is Read-Only");
		}
	}
	
	public ContinuousMeasurementPresentation(T bean) {
		super(bean);
	}

	public AbstractValueModel getLabelModel() {
		return new LabelModel();
	}
	
	public String toString() {
		return (String) getLabelModel().getValue(); 
	}
	
	public String normConfIntervalString() {
		DecimalFormat df = new DecimalFormat("##0.0##");
		NormalDistribution distribution = new NormalDistributionImpl(getBean().getMean(), getBean().getStdDev());
		Interval<Double> confInterval;
		try {
			confInterval = new Interval<Double>(distribution.inverseCumulativeProbability(0.025),
					distribution.inverseCumulativeProbability(0.975));
		} catch (MathException e) {
			e.printStackTrace();
			return null;
		}

		return df.format(getBean().getMean()) + 
				" (" + df.format(confInterval.getLowerBound()) + ", " + df.format(confInterval.getUpperBound()) + ")";
	}
}
