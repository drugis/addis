package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import org.drugis.addis.entities.relativeeffect.Distribution;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;



@SuppressWarnings("serial")
public class DistributionPresentation extends PresentationModel<Distribution> implements LabeledPresentationModel {

	public class LabelModel extends AbstractValueModel implements PropertyChangeListener {
		public LabelModel() {
			getBean().addPropertyChangeListener(this);
		}
		
		public void propertyChange(PropertyChangeEvent evt) {
			firePropertyChange("value", null, getValue());
		}

		public Object getValue() {
					
			DecimalFormat format = new DecimalFormat("###0.00");
			
			return format.format(getBean().getQuantile(0.5)) + " (" + format.format(getBean().getQuantile(0.025)) + ", " + 
				format.format(getBean().getQuantile(0.975)) + ")";
		}

		public void setValue(Object arg0) {
			throw new RuntimeException();
		}
	}
	
	public DistributionPresentation(Distribution bean) {
		super(bean);
	}
	
	public AbstractValueModel getLabelModel() {
		return new LabelModel();
	}
	
	@Override
	public String toString() {
		return (String) getLabelModel().getValue();
	}

}
