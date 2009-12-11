package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.Endpoint;

@SuppressWarnings("serial")
public class EndpointCreationModel extends EndpointPresentationModel {
	public EndpointCreationModel(Endpoint bean) {
		super(bean, null);
		
		getModel(Endpoint.PROPERTY_TYPE).addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (fromTo(event, Endpoint.Type.RATE, Endpoint.Type.CONTINUOUS) &&
						getUOM().equals(Endpoint.UOM_DEFAULT_RATE)) {
					setUOM(Endpoint.UOM_DEFAULT_CONTINUOUS);
				} else if (fromTo(event, Endpoint.Type.CONTINUOUS, Endpoint.Type.RATE) &&
						getUOM().equals(Endpoint.UOM_DEFAULT_CONTINUOUS)) {
					setUOM(Endpoint.UOM_DEFAULT_RATE);
				}
			}
			
			private boolean fromTo(PropertyChangeEvent event, Endpoint.Type from, Endpoint.Type to) {
				return event.getNewValue().equals(to) && event.getOldValue().equals(from);
			}

			private void setUOM(String val) {
				getModel(Endpoint.PROPERTY_UNIT_OF_MEASUREMENT).setValue(val);
			}

			private Object getUOM() {
				return getModel(Endpoint.PROPERTY_UNIT_OF_MEASUREMENT).getValue();
			}});
	}
}
