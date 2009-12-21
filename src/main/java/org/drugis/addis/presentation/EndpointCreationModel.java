package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.AbstractOutcomeMeasure;
import org.drugis.addis.entities.Endpoint;

@SuppressWarnings("serial")
public class EndpointCreationModel extends EndpointPresentationModel {
	public EndpointCreationModel(Endpoint bean) {
		super(bean, null);
		
		getModel(Endpoint.PROPERTY_TYPE).addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (fromTo(event, AbstractOutcomeMeasure.Type.RATE, AbstractOutcomeMeasure.Type.CONTINUOUS) &&
						getUOM().equals(AbstractOutcomeMeasure.UOM_DEFAULT_RATE)) {
					setUOM(AbstractOutcomeMeasure.UOM_DEFAULT_CONTINUOUS);
				} else if (fromTo(event, AbstractOutcomeMeasure.Type.CONTINUOUS, AbstractOutcomeMeasure.Type.RATE) &&
						getUOM().equals(AbstractOutcomeMeasure.UOM_DEFAULT_CONTINUOUS)) {
					setUOM(AbstractOutcomeMeasure.UOM_DEFAULT_RATE);
				}
			}
			
			private boolean fromTo(PropertyChangeEvent event, Endpoint.Type from, Endpoint.Type to) {
				return event.getNewValue().equals(to) && event.getOldValue().equals(from);
			}

			private void setUOM(String val) {
				getModel(AbstractOutcomeMeasure.PROPERTY_UNIT_OF_MEASUREMENT).setValue(val);
			}

			private Object getUOM() {
				return getModel(AbstractOutcomeMeasure.PROPERTY_UNIT_OF_MEASUREMENT).getValue();
			}});
	}
}
