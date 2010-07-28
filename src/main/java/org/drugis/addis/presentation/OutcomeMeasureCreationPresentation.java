/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Variable;

@SuppressWarnings("serial")
public class OutcomeMeasureCreationPresentation extends VariablePresentation {
	public OutcomeMeasureCreationPresentation(OutcomeMeasure bean) {
		super(bean, null, null);
		
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
			}
		});
	}
}
