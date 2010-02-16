package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Variable;

@SuppressWarnings("serial")
public class OutcomeMeasureCreationModel extends VariablePresentationModel {
	public OutcomeMeasureCreationModel(OutcomeMeasure bean) {
		super(bean, null);
		
		getModel(Endpoint.PROPERTY_TYPE).addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (fromTo(event, Variable.Type.RATE, Variable.Type.CONTINUOUS) &&
						getUOM().equals(Variable.UOM_DEFAULT_RATE)) {
					setUOM(Variable.UOM_DEFAULT_CONTINUOUS);
				} else if (fromTo(event, Variable.Type.CONTINUOUS, Variable.Type.RATE) &&
						getUOM().equals(Variable.UOM_DEFAULT_CONTINUOUS)) {
					setUOM(Variable.UOM_DEFAULT_RATE);
				}
			}
			
			private boolean fromTo(PropertyChangeEvent event, Variable.Type from, Variable.Type to) {
				return event.getNewValue().equals(to) && event.getOldValue().equals(from);
			}

			private void setUOM(String val) {
				getModel(Variable.PROPERTY_UNIT_OF_MEASUREMENT).setValue(val);
			}

			private Object getUOM() {
				return getModel(Variable.PROPERTY_UNIT_OF_MEASUREMENT).getValue();
			}});
	}
}
