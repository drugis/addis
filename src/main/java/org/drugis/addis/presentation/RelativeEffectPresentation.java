package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.RelativeEffect;
import org.drugis.common.Interval;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class RelativeEffectPresentation extends PresentationModel<RelativeEffect<? extends Measurement>> implements LabeledPresentationModel {
	
	public class LabelModel extends AbstractValueModel implements PropertyChangeListener {
		public LabelModel() {
			getBean().addPropertyChangeListener(this);
		}
		
		public void propertyChange(PropertyChangeEvent evt) {
			firePropertyChange("value", null, getValue());
		}

		public Object getValue() {
			DecimalFormat format = new DecimalFormat("###0.00");
			Interval<Double> ci = getBean().getConfidenceInterval();
			if (getBean().getRelativeEffect().equals(Double.NaN) || 
					ci.getLowerBound().equals(Double.NaN) ||
					ci.getUpperBound().equals(Double.NaN)) {
				return "N/A";
			}
			return format.format(getBean().getRelativeEffect()) + " (" + format.format(ci.getLowerBound()) + ", " + 
				format.format(ci.getUpperBound()) + ")";
		}

		public void setValue(Object arg0) {
			throw new RuntimeException();
		}
	}

	public RelativeEffectPresentation(RelativeEffect<? extends Measurement> bean) {
		super(bean);
	}
	
	public AbstractValueModel getLabelModel() {
		return new LabelModel();
	}
}
