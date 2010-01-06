package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.Measurement;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;


public class MeasurementPresentationModel extends PresentationModel<Measurement> {
	private static final long serialVersionUID = -1122087193288204443L;
	
	private AbstractValueModel d_pgSize;
	
	public MeasurementPresentationModel(Measurement bean, AbstractValueModel abstractValueModel) {
		super(bean);
		d_pgSize = abstractValueModel;
		this.addBeanPropertyChangeListener(new MeasurementChangeListener());
		d_pgSize.addPropertyChangeListener(new MeasurementChangeListener());
	}
	
	private class MeasurementChangeListener implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			int mySize = (Integer) getValue("sampleSize");
			if (evt.getSource() == d_pgSize) {
				setValue("sampleSize", d_pgSize.getValue());
			}
			else if (mySize > (Integer) d_pgSize.getValue()){
				setValue("sampleSize", d_pgSize.getValue());
			}
		}
	}
}
