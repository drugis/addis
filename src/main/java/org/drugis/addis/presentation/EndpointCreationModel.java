package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.OutcomeMeasure;

@SuppressWarnings("serial")
public class EndpointCreationModel extends EndpointPresentationModel {
	public EndpointCreationModel(Endpoint bean) {
		super(bean, null);
		
		getModel(Endpoint.PROPERTY_TYPE).addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (fromTo(event, OutcomeMeasure.Type.RATE, OutcomeMeasure.Type.CONTINUOUS) &&
						getUOM().equals(OutcomeMeasure.UOM_DEFAULT_RATE)) {
					setUOM(OutcomeMeasure.UOM_DEFAULT_CONTINUOUS);
				} else if (fromTo(event, OutcomeMeasure.Type.CONTINUOUS, OutcomeMeasure.Type.RATE) &&
						getUOM().equals(OutcomeMeasure.UOM_DEFAULT_CONTINUOUS)) {
					setUOM(OutcomeMeasure.UOM_DEFAULT_RATE);
				}
			}
			
			private boolean fromTo(PropertyChangeEvent event, Endpoint.Type from, Endpoint.Type to) {
				return event.getNewValue().equals(to) && event.getOldValue().equals(from);
			}

			private void setUOM(String val) {
				getModel(OutcomeMeasure.PROPERTY_UNIT_OF_MEASUREMENT).setValue(val);
			}

			private Object getUOM() {
				return getModel(OutcomeMeasure.PROPERTY_UNIT_OF_MEASUREMENT).getValue();
			}});
	}
}
